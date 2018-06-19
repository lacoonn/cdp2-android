package com.example.team4kb.smartbabymonitor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 2018-06-12.
 */

public class Jsonfactory {
    private String type;
    private String filename;
    private int flag = 100;
    private JSONObject obj;
    private long size;

    public Jsonfactory(String type)
    {
        this.type=type;

        if(type.equals("list"))
        {
            flag = 0;
        }

        manufactureJson();
    }

    public Jsonfactory(String type, String filename)
    {
        this.type=type;
        this.filename=filename;

        if(type.equals("delete"))
        {
            flag = 1;
        }
        else if(type.equals("play"))
        {
            flag = 2;
        }

        manufactureJson();
    }

    public Jsonfactory(String type, String filename,long size)
    {
        this.type=type;
        this.filename=filename;
        this.size = size;

        if(type.equals("upload"))
        {
            flag = 3;
        }

        manufactureJson();
    }


    private void manufactureJson()
    {
        if(flag == 0)
        {
            obj = new JSONObject();
            try {
                obj.put("message", "list");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(flag == 1)
        {
            obj = new JSONObject();
            try {
                obj.put("message", "delete");
                obj.put("filename",filename);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(flag == 2)
        {
            obj = new JSONObject();
            try {
                obj.put("message", "play");
                obj.put("filename",filename);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(flag == 3)
        {
            obj = new JSONObject();
            try {
                obj.put("message", "upload");
                obj.put("filename",filename);
                obj.put("size",size);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getobject()
    {
        return obj.toString();
    }
}
