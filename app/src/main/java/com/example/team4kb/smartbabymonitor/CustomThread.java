package com.example.team4kb.smartbabymonitor;

/**
 * Created by User on 2018-06-12.
 */

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CustomThread extends Thread {
    private String value;
    private String returnstring;
    private BufferedReader networkReader;

    public CustomThread(String value, BufferedReader networkReader) {
        this.value = value;
        this.networkReader=networkReader;
    }

    public void run() {
        try {
            Log.w("CustomThread", "Start Thread");

            String line="";

            // 서버로부터 명령어 라인을 읽어온다
            if(value.equals("read"))
            {
                while (line.length()==0) {
                    Log.w("Open READLINE", "OK");
                    line = networkReader.readLine();
                    Log.w("Finish READLINE", "OK");
                }

                // returnstring에 받아온 명령어를 저장한다
                returnstring = line;
                Log.w("return string", returnstring);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w("CustomThread", "Finish Thread");
    }

    public String getReturnstring()
    {
        return returnstring;
    }

}
