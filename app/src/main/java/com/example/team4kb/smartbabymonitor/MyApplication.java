package com.example.team4kb.smartbabymonitor;

import android.app.Application;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by User on 2018-06-12.
 */

public class MyApplication extends Application {
    private String gip = "doctor15.iptime.org";
    private int gport = 9999;

    private static Socket socket;

    private static BufferedReader networkReader;
    private static BufferedWriter networkWriter;

    public MyApplication()
    {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            setSocket();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setSocket() throws IOException {
        try {
            socket = new Socket(gip, gport);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF8"));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public void shutdownSocket()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket()
    {
        return socket;
    }
    public BufferedReader getBufferedReader()
    {
        return networkReader;
    }
    public BufferedWriter getBufferedWriter()
    {
        return networkWriter;
    }
    public String getGip(){return gip;}
}
