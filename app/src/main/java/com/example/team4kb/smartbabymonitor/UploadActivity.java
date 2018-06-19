package com.example.team4kb.smartbabymonitor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {
    private Context mContext = this;
    private MyApplication myApp;
    private String mFileName;
    private long filesize;
    private static Socket uploadSocket;
    private BufferedReader networkReader;
    private BufferedWriter networkWriter;
    private String ip;
    private int port = 10000;
    private String mRoot;
    private TextView mPath;
    private String pathSave;
    private ListView lvFileControl;
    private List<String> lItem = null;
    private List<String> lPath = null;

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(UploadActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage(getString(R.string.sending));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            Log.d("progressDiaglog", "delete");
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mPath = (TextView) findViewById(R.id.tvPath);
        lvFileControl = (ListView) findViewById(R.id.lvFileControl);
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();

        myApp = (MyApplication) getApplication();
        networkReader = myApp.getBufferedReader();
        networkWriter = myApp.getBufferedWriter();
        ip = myApp.getGip();

        getDir(mRoot);

        lvFileControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(lPath.get(position));

                if (file.isDirectory()) {
                    if (file.canRead())
                        getDir(lPath.get(position));
                    else {
                        Toast.makeText(mContext, "No files in this folder.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mFileName = file.getName();
                    filesize = file.length();
                    Log.d("file length", "size is " + filesize);

                    new AlertDialog.Builder(mContext)
                            .setTitle("확인")
                            .setMessage("업로드 하시겠습니까?")
                            .setIcon(android.R.drawable.ic_menu_save)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Log.d("upload", "start");

                                    CustomThread read_thread = new CustomThread("read", networkReader);
                                    read_thread.start();
                                    Log.d("upload", "read thread start");

                                    Jsonfactory pla = new Jsonfactory("upload", mFileName, filesize);

                                    String obj = pla.getobject();
                                    if (obj != null) {
                                        PrintWriter out = new PrintWriter(networkWriter, true);
                                        out.println(obj);
                                    }

                                    try {
                                        read_thread.join();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("upload", "read thread finish");

                                    Log.d("upload", "upload thread start");
                                    final UploadTask uploadTask = new UploadTask(UploadActivity.this);
                                    uploadTask.execute("uploadTask");
                                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            uploadTask.cancel(true);
                                            // 취소시 처리 로직
                                            Toast.makeText(mContext, "업로드를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // 취소시 처리 로직
                                    Toast.makeText(mContext, "업로드를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void getDir(String dirPath) {
        pathSave = dirPath;
        mPath.setText("Location: " + dirPath);
        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(mRoot)) {
            lItem.add("../");
            lPath.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            lPath.add(file.getAbsolutePath());
            if (file.isDirectory())
                lItem.add(file.getName() + "/");
            else
                lItem.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.upload_item, lItem);
        lvFileControl.setAdapter(fileList);
    }

    private class UploadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String returnstring = null;
            try {
                try {
                    setUploadSocket(ip, port);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    PrintWriter outs = new PrintWriter(new BufferedWriter(new OutputStreamWriter(uploadSocket.getOutputStream())), true);

                    String temp = pathSave + "/" + mFileName;
                    Log.w("File_Json_hong", temp);
                    DataInputStream dis = new DataInputStream(new FileInputStream(new File(temp)));
                    DataOutputStream dos = new DataOutputStream(uploadSocket.getOutputStream());

                    byte[] buf = new byte[4096];
                    int read_lens = 0;
                    int total = 0;
                    while ((read_lens = dis.read(buf)) > 0) {
                        dos.flush();
                        dos.write(buf, 0, read_lens);
                        dos.flush();
                        // publish progress
                        total += read_lens;
                        publishProgress((int) (total * 100 / filesize));
                    }
                    dos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        uploadSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // 업로드 후 완료했다는 연락을 기다린다
                String line = "";
                while (line.length() == 0) {
                    Log.w("Open READLINE", "OK");
                    line = networkReader.readLine();
                    Log.w("Finish READLINE", "OK");
                }

                // returnstring에 받아온 명령어를 저장한다
                returnstring = line;
                Log.w("return string", returnstring);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return returnstring;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mWakeLock.release();
            hideProgressDialog();

            finish();
            Log.d("upload", "upload thread finish");
            Toast.makeText(mContext, "업로드를 완료했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setUploadSocket(String ip, int port) throws IOException {
        try {
            uploadSocket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(uploadSocket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(uploadSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }
}
