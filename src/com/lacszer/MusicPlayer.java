package com.lacszer;

import java.io.*;
import javax.sound.sampled.*;

public class MusicPlayer {
    Clip clip;
    AudioInputStream audio;
    boolean music_on;//音乐的打开标志，用于防止音乐多开

    public MusicPlayer(String filename) {
        try {
            File music = new File(filename);
            if(music.exists()) {
                audio = AudioSystem.getAudioInputStream(music);
                clip = AudioSystem.getClip();
                clip.open(audio);
                music_on = false;
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loopPlay(){//循环播放
        if(!music_on) {
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            music_on = true;//防止多次打开同一个音乐
        }
    }
    public void playOnce() {//从头播放一次
        if(!music_on) {
            clip.setMicrosecondPosition(0);//设定为从头开始播放
            clip.start();
            music_on = true;
        }
    }
    public void playOnce(boolean canRepeat) {//从头播放一次，且可以多开
        clip.setMicrosecondPosition(0);//设定为从头开始播放
        clip.start();
        music_on = true;
    }

    public void stopPlay() {// 关闭音乐
        music_on = false;
        clip.stop();//停止线程
    }
}

