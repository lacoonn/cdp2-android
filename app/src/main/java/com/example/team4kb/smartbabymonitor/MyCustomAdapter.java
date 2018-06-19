package com.example.team4kb.smartbabymonitor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by User on 2018-06-12.
 */

public class MyCustomAdapter extends ArrayAdapter<String> {
    int groupid;
    ArrayList<String> item_list;
    ArrayList<String> desc;
    Context context;

    public MyCustomAdapter(Context context, int vg, int id, ArrayList<String> item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }
    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView textview;
        public Button button;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the list_item.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textview= (TextView) rowView.findViewById(R.id.filelist_item_name);
            viewHolder.button= (Button) rowView.findViewById(R.id.filelist_item_delete_button);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textview.setText(item_list.get(position));
        holder.button.setText("delete");
        holder.button.setTag(position);

        String actName = context.getClass().getSimpleName().trim();
        Log.d("myCustomAdaptor","context is "+actName);
        // 텍스트를 클릭할 수 있게 한다.
        // 호출한 액티비티가 FileListActivity이면 삭제 버튼을 지운다.
        if (actName.equals("FileListActivity"))
        {
            Log.d("myCustomAdaptor","file list activity");
            holder.textview.setEnabled(true);
            holder.button.setVisibility(View.GONE);
        }
        // 텍스트를 클릭할 수 있게 한다.
        // 호출한 액티비티가 FileListActivity2이면 삭제 버튼을 활성화한다.
        if (actName.equals("FileListActivity2"))
        {
            Log.d("myCustomAdaptor","file list activity");
            holder.textview.setEnabled(false);
            holder.button.setVisibility(View.VISIBLE);
        }

        return rowView;
    }
}
