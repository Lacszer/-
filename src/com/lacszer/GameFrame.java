package com.lacszer;

import com.sun.prism.impl.TextureResourcePool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CountDownLatch;

public class GameFrame extends JFrame {
    final int Hz60 = 16;
    final int WIDTH = 650, HEIGHT = 1000;
    final int L = 116, M = 280, R = 435;//三条道路的定位
    final int KeyLeft = 37, KeyRight = 39, KeyUp = 38, KeyDown = 40;//方向键键码
    boolean gameOver; //游戏结束标志
    MusicPlayer background = new MusicPlayer("music\\Background.wav");//背景音乐
    Container cont;//中间层容器
//    Container layer = getLayeredPane();//底层容器
    CountDownLatch cgPlaying = new CountDownLatch(1);//用创建计数为n的线程计数器

    gameTexture roadLineL = new gameTexture(82, 0, "Texture\\roadLine.png");
    gameTexture roadLineR = new gameTexture(550, 0, "Texture\\roadLine.png");
    //左右碰撞检测条
    gameTexture frameLineT = new gameTexture(0, 0, "Texture\\frameLine.png");
    gameTexture frameLineB = new gameTexture(0, HEIGHT, "Texture\\frameLine.png");
    //上下碰撞检测条

    Orga_Itsuka OrgaItsuka;//奥尔加


    int speed = 2;//马路滚动速度

    public GameFrame() {
        super("卡其脱离太");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        //获取屏幕分辨率
        setLocation(gd.getDisplayMode().getWidth() / 2, 10);//确保位置在屏幕正中心
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        //以上为框架初始化

        gameOver = false;

        cont = getLayeredPane();//getContentPane();
        cont.setLayout(null);//设置容器为空布局

    }

    public void startGame() {

        new CG().run();//播放CG

        cont.add(roadLineL);
        cont.add(roadLineR);
        //添加左右碰撞检测条
        cont.add(frameLineT);
        cont.add(frameLineB);
        //添加上下碰撞检测条

        background.loopPlay();
        OrgaItsuka = new Orga_Itsuka();
        OrgaItsuka.start();

        new Thread() { //路面加速
            @Override
            public void run() {
                try {
                    cgPlaying.await();
                } catch (Exception e) {}

                int i = 5;
                while (speed <= 10) {//20为加把劲骑士道具的速度
                    ++speed;
                    ++i;
                    try {
                        this.sleep(1000 * i);//每5秒加速一次
                    } catch (Exception e) {}
                }
            }
        }.start();

        new Thread(){ //生成敌人
            @Override
            public void run() {
                try {
                    cgPlaying.await();
                } catch (Exception e) {}

                while (!gameOver)
                {
                    if (Math.random() * 60 < speed) {//几率为50分之speed
                        System.out.println("泥头车来咯！");

                        switch ((int)(Math.random() * 30 % 3)) {
                            case 0: new Enemy(L).start(); break;
                            case 1: new Enemy(M).start(); break;
                            case 2: new Enemy(R).start(); break;
                        }
                    }

                    try {this.sleep((220 / (speed + 2)) * Hz60);} catch (Exception e) {}//生成的最小间隔为一辆车开进地图的时间
                }
            }
        }.start();

        new Road().start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("微软雅黑",Font.LAYOUT_LEFT_TO_RIGHT, 40));
//        g.drawString("距离：", 380, 78);
    }

    class Road extends Thread {//DNAj街道滚轴
        gameTexture rollRoad = new gameTexture(-10, 0, "Texture\\rollRoad.png");
        gameTexture rollRoad2 = new gameTexture(-10, HEIGHT, "Texture\\rollRoad.png");
        //路面滚动轴

        public Road() {
            cont.add(rollRoad, JLayeredPane.DEFAULT_LAYER);
            cont.add(rollRoad2, JLayeredPane.DEFAULT_LAYER);
        }

        @Override
        public void run() {
            try {
                cgPlaying.await();
            } catch (Exception e) {}

            while (!gameOver) {
                try {
                    rollRoad.resetXY(rollRoad.getX(), rollRoad.getY() - speed);
                    rollRoad2.resetXY(rollRoad2.getX(), rollRoad2.getY() - speed);
                    if (rollRoad2.getY() <= 0) rollRoad.resetXY(rollRoad.getX(), rollRoad2.getY() + rollRoad2.height);
                    if (rollRoad.getY() <= 0) rollRoad2.resetXY(rollRoad2.getX(), rollRoad.getY() + rollRoad.height);

                    this.sleep(Hz60);
                } catch (Exception e) {}
            }
        }
    }

    class Orga_Itsuka extends Thread {
        int OrgaX = M - 20, OrgaY = 0;
        gameTexture Orga = new gameTexture(OrgaX, OrgaY, "Texture\\Orga.gif" );
        gameTexture Orgasize = new gameTexture(OrgaX + 30, OrgaY + 130, "Texture\\Orgasize.png");//碰撞体积
        int OrgaSpeed = 1;
        boolean OrgaL, OrgaR, OrgaU, OrgaD;

        public Orga_Itsuka() {
           cont.add(Orga);
           cont.add(Orgasize);
            OrgaL = false;
            OrgaR = false;
            OrgaU = false;
            OrgaD = false;
        }

        @Override
        public void run() {
            try {
                cgPlaying.await();
            } catch (Exception e) {}

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    switch (e.getKeyCode()) {
                        case KeyLeft: OrgaL = true; break;
                        case KeyRight: OrgaR = true; break;
                        case KeyUp: OrgaU = true; break;
                        case KeyDown: OrgaD = true; break;
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    super.keyReleased(e);
                    switch (e.getKeyCode()) {
                        case KeyLeft: OrgaL = false; break;
                        case KeyRight: OrgaR = false; break;
                        case KeyUp: OrgaU = false; break;
                        case KeyDown: OrgaD = false; break;
                    }
                }
            });

            while (!gameOver) {

                if (OrgaL) OrgaX -= OrgaSpeed;
                if (OrgaR) OrgaX += OrgaSpeed;
                if (OrgaU) OrgaY -= OrgaSpeed;
                if (OrgaD) OrgaY += OrgaSpeed;


                Orga.resetXY(OrgaX, OrgaY);//重绘
                Orgasize.resetXY(OrgaX + 30, OrgaY + 130);//重绘碰撞体积

                if (Orga.getBounds().intersects(roadLineL.getBounds())) OrgaX = roadLineL.getX() + 1;//与左边线碰撞
                if (Orga.getBounds().intersects(roadLineR.getBounds())) OrgaX = roadLineR.getX() - Orga.width - 2;//与右边线碰撞
                if (Orga.getBounds().intersects(frameLineT.getBounds())) OrgaY = 1;//与上边线碰撞
                if (Orga.getBounds().intersects(frameLineB.getBounds())) OrgaY = HEIGHT - Orga.height - 2;//与上边线碰撞

                Orga.resetXY(OrgaX, OrgaY);//重绘
                Orgasize.resetXY(OrgaX + 30, OrgaY + 130);//重绘碰撞体积

                OrgaSpeed = (speed - 2) > 10 ? 10: (speed - 2);//移动速度加成
                try {this.sleep(Hz60);} catch (Exception e){}
            }

            //游戏结束的事件
            Orga.setIcon(new ImageIcon("Texture\\hopeFlower.gif"));
            System.out.println("寄！");
            background.stopPlay();
            new MusicPlayer("music\\hopeFlower.wav").playOnce();

        }
    }

    class Enemy extends Thread {//黑色高级车类
        gameTexture enemy;

        public Enemy(int location) {
            enemy = new gameTexture(location, HEIGHT, "Texture\\blackCar.png");
            cont.add(enemy, JLayeredPane.DRAG_LAYER);
        }

        @Override
        public void run() {
            while (!gameOver) {
                enemy.resetXY(enemy.getX(), enemy.getY() - speed - 2);
                if (enemy.getBounds().intersects(OrgaItsuka.Orgasize.getBounds())) gameOver = true;//撞到车即游戏结束
                try {this.sleep(Hz60);} catch (Exception e) {}
            }
        }

    }

    class CG extends Thread{
        gameTexture cg = new gameTexture(0, HEIGHT / 4, "Texture\\cg.gif");
        MusicPlayer cgMusic = new MusicPlayer("music\\cg.wav");
        KeyAdapter anyKey;
        boolean isend= false;

        public CG() {
            anyKey = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    super.keyPressed(e);
                    switch (e.getKeyCode()) {
                        default: isend = true; break;
                    }
                }
            };
        }

        @Override
        public void run() {
            addKeyListener(anyKey);

            cont.add(cg);
            try {Thread.sleep(500);} catch (Exception e){}//解决音画不同步的问题
            cgMusic.playOnce();
            long time = System.currentTimeMillis();

            while (true) {
                System.out.println("监听任意键中"); //不知道为什么不写就无法实现监听

                if (System.currentTimeMillis() - time == 13500 || isend) {
                    cgPlaying.countDown();//计时器解除
                    cont.remove(cg);
                    cgMusic.stopPlay();
                    removeKeyListener(anyKey);
                    break;
                }
            }

        }

    }

}

