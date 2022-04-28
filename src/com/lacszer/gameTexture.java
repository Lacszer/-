package com.lacszer;

import javax.swing.*;

public class gameTexture extends JLabel {
    int width;
    int height;


    public gameTexture(int x, int y, String imagePath) {
        super(new ImageIcon(imagePath));//建立图片标签
        this.setLayout(null);//设置空布局
        width = this.getIcon().getIconWidth();
        height = this.getIcon().getIconHeight();

        this.setBounds(x, y, width, height);//设置框架的相对位置以及宽高


    }

    public gameTexture(gameTexture GT) {
        super(GT.getIcon());

        this.width = GT.width;
        this.height = GT.height;

        this.setLayout(null);//设置空布局
        this.setBounds(GT.getX(), GT.getY(), this.width, this.height);//设置框架的相对位置以及宽高

    }

    public void resetXY(int x, int y) {
        this.setBounds(x, y, width, height);//设置框架的相对位置以及宽高
    }
}
