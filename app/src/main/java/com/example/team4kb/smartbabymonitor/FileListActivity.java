package com.example.team4kb.smartbabymonitor;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {
    private String ob;
    ArrayList<String> list;
    private MyApplication myApp;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private MyCustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filelist);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        myApp = (MyApplication) getApplication();
        networkReader = myApp.getBufferedReader();
        networkWriter = myApp.getBufferedWriter();

    }

    @Override
    protected void onResume() {
        super.onResume();

        CustomThread list_listen_thread = new CustomThread("read",networkReader);
        list_listen_thread.start();

        Jsonfactory jfc = new Jsonfactory("list");
        String obj = jfc.getobject();
        Log.w("List_Json", obj);

        if(obj != null) {
            PrintWriter out = new PrintWriter(networkWriter, true);
            out.println(obj);
        }

        ListView lstview=(ListView)findViewById(R.id.listview_filelist);
        lstview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Toast.makeText(FileListActivity.this, "Play " + list.get(position),Toast.LENGTH_LONG).show();

                Jsonfactory pla= new Jsonfactory("play",list.get(position));

                String obj = pla.getobject();

                Log.w("Play_Json", obj);

                if(obj != null) {
                    PrintWriter out = new PrintWriter(networkWriter, true);
                    out.println(obj);
                }
            }
        });

        try {
            list_listen_thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ob = list_listen_thread.getReturnstring();
        list = new ArrayList<String>();

        try {
            JSONObject jsonObject = new JSONObject(ob);
            JSONArray jarray = jsonObject.getJSONArray("files");   // JSONArray 생성
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                String name = jObject.getString("name");

                list.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new MyCustomAdapter(this,R.layout.filelist_item,R.id.filelist_item_name,list);

        lstview.setAdapter(adapter);
    }
}
