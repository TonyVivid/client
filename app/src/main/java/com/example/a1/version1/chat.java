package com.example.a1.version1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import trans_data.chatInfo;

public class chat extends AppCompatActivity {
    Handler handler;
 chatInfo myinfo=null;
 TextView txt_chatInfo=null;
 EditText edt=null;
 String txt_info="";
String sender="";
    @Override
    protected void onPause() {
        if(getbackMessage!=null && getbackMessage.getStatus() == AsyncTask.Status.RUNNING){
            getbackMessage.cancel(true);
        }
        super.onPause();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        txt_chatInfo=findViewById(R.id.txt_chatInfo);
        edt=findViewById(R.id.txt_input);
        getbackMessage.execute();
        init();
        handler=new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==666)
                {
                    new chatThread().start();
                }
                if(msg.what==667){

                    TextView txt_header=(TextView) findViewById(R.id.txt_header);
                    String sender=txt_header.getText().toString();
                    txt_info=txt_info+"\n"+myinfo.getInfo();
                    txt_chatInfo.setText(txt_info);
                }
                if(msg.what==668){
                    edt.setText("");
                    txt_chatInfo.setText(txt_info);
                }
            }
        };
    }
public void updateUI(){

}
    public void init(){
        String owner=getIntent().getStringExtra("owner");
        chatInfo info =(chatInfo) getIntent().getSerializableExtra("chatinfo");
        if(info!=null){
            TextView txt_header=(TextView) findViewById(R.id.txt_header);
            txt_header.setText(info.getSender());
            txt_chatInfo.setText(info.getInfo());
        }
        else {
            TextView txt_header = (TextView) findViewById(R.id.txt_header);
            txt_header.setText(owner);
        }

    }

AsyncTask<Void,String,Void> getbackMessage=new AsyncTask<Void, String, Void>() {
    @Override
    protected Void doInBackground(Void... voids) {

        DataInputStream in= ((GlobalSocket)getApplication()).getIn();
        DataOutputStream out=((GlobalSocket)getApplication()).getOut();
        ObjectInputStream objin=((GlobalSocket)getApplication()).getObjin();
        ObjectOutputStream objout=((GlobalSocket)getApplication()).getObjout();

        try {
            while(true) {
                Log.d("chatState",this.getStatus().toString());
                if(isCancelled())
                    return null;
                int type = in.readInt();
                if (type == 445) {
                    chatInfo info = (chatInfo) objin.readObject();
                    Log.d("TAG", "receive the message");
                    myinfo = info;
                    Message msg=Message.obtain();
                    msg.what=667;
                    handler.sendMessage(msg);
                }
            }
        }catch (Exception e){

            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void Void) {
        super.onPostExecute(Void);
    }
};

class chatThread extends Thread{
        Socket socket=null;
        DataOutputStream out=null;
        DataInputStream in=null;
        ObjectOutputStream objout=null;
        ObjectInputStream objin=null;
        chatInfo info=null;
        public void run(){

            try{

                Socket socket=((GlobalSocket)getApplication()).getSocket();
                in=((GlobalSocket)getApplication()).getIn();
                out=((GlobalSocket)getApplication()).getOut();
                objin=((GlobalSocket)getApplication()).getObjin();
                objout=((GlobalSocket)getApplication()).getObjout();
                String sender=((GlobalSocket)getApplication()).getName();
                EditText txt_input=(EditText) findViewById(R.id.txt_input);
                TextView txt_header=(TextView)findViewById(R.id.txt_header);
                String receiver=txt_header.getText().toString().trim();
                String txt=txt_input.getText().toString();
                chatInfo info=new chatInfo();
                Date date = new Date();
                String now=date.toLocaleString();
                txt=sender+":  "+now+"\n"+txt;
                info.setInfo(txt);
                info.setReceiver(receiver);
                info.setSender(sender);
                txt_info=txt_info+"\n"+txt;

                out.writeInt(33);
                Log.d("TAg","!!!!!");

                if(info==null){
                    return;
                }
                else {
                    objout.writeObject(info);
                    objout.flush();
                    Message msg=Message.obtain();
                    msg.what=668;
                    handler.sendMessage(msg);
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }
}
    public void  sendInfo(View view){
       Message msg=Message.obtain();
       msg.what=666;
       handler.sendMessage(msg);

    }
}
