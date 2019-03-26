package com.example.a1.version1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.GuardedObject;
import java.util.HashMap;
import java.util.List;

import trans_data.carriage;
import trans_data.carriageList;
import trans_data.chatInfo;
import trans_data.loginInfo;
import trans_data.picture;
import trans_data.transSign;

public class MainActivity extends AppCompatActivity {
     public  static MainActivity mainactivity;
    LoginThread thread=new LoginThread();
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainactivity=this;
         handler=new Handler(){
            public  void handleMessage(Message msg){
                if(msg.what==333){
                    TextView txt_name = (TextView) findViewById(R.id.txt_name);
                    TextView txt_password = (TextView) findViewById(R.id.txt_password);
                    txt_name.setText("");
                    txt_password.setText("");
                    Toast.makeText(getApplication(),"the number is logged in",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
    public LoginThread getThread(){
        return this.thread;
    }
    protected  void onStart(){

        super.onStart();
    }
    protected  void onPause(){
       super.onPause();
    }
    class LoginThread extends  Thread {
        int MSG=0;
        chatInfo info=null;
       LoginThread(){}
       LoginThread(int MSG){
           this.MSG=MSG;
       }
       public void setInfo(chatInfo info){
           this.info=info;
       }
       public void setMSG(int MSG){
           this.MSG=MSG;
       }

        DataInputStream in = null;
        DataOutputStream out = null;
        Socket my_socket = null;
        ObjectInputStream input=null;
        ObjectOutputStream output=null;
        public void run() {
            Log.d("TAG", "begin to connect");
            try {
                my_socket=new Socket("192.168.43.72",12345);
                Log.d("TAG",my_socket.getLocalAddress().toString()+"   "+String.valueOf(my_socket.getLocalPort()));
                ((GlobalSocket)getApplication()).setAddress(my_socket.getLocalAddress());
                ((GlobalSocket)getApplication()).setPort(my_socket.getLocalPort());
                //Log.d("TAG",s1.getAddress().toString()+"   "+String.valueOf(s1.getLocalPort()));
                this.in = new DataInputStream(my_socket.getInputStream());
                this.out = new DataOutputStream(my_socket.getOutputStream());
                this.output = new ObjectOutputStream(out);
                this.input=new ObjectInputStream(in);
                ((GlobalSocket)getApplication()).setSocket(my_socket);
                ((GlobalSocket)getApplication()).setIn(in);
                ((GlobalSocket)getApplication()).setOut(out);
                ((GlobalSocket)getApplication()).setObjin(input);
                ((GlobalSocket)getApplication()).setObjout(output);
                if(MSG==100)
                    LoginMessage();
                if(MSG==101){
                    SendInfo();
                }

            } catch (IOException e) {
                Log.d("TAG", e.getMessage());
            }
        }
        private void SendInfo(){
            try{

                out.writeInt(33);
                output.writeObject(info);
                output.flush();

            }
            catch(Exception e){
                e.printStackTrace();
            }


        }
        private void LoginMessage() {
            try {
                TextView txt_name = (TextView) findViewById(R.id.txt_name);
                TextView txt_password = (TextView) findViewById(R.id.txt_password);
                String number = txt_name.getText().toString();
                String password = txt_password.getText().toString();
                ((GlobalSocket)getApplication()).setName(number);
                loginInfo user = new loginInfo();
                user.setMsgInfo(1111);
                user.setNumber(number);
                user.setPassword(password);
                out.writeInt(32);
                output.writeObject(user);
                output.flush();

                Log.d("TAG", user.getNumber() + user.getPassword() + user.getMsgInfo());
                Boolean result=in.readBoolean();
                if (result == true) {
                    Log.d("TAG","return true");
                    loginInfo user1 = new loginInfo();
                   user1.setMsgInfo(1114);
                    user1.setNumber(number);
                    user1.setPassword(password);
                    out.writeInt(32);
                   output.writeObject(user1);
                   output.flush();
                    try {
                        carriageList my_carriagelist = (carriageList) input.readObject();
                        carriage my_carriage= my_carriagelist.getList().get(0);
                        List<picture> pic=my_carriage.getPicture();
                        Intent intent=new Intent(MainActivity.this,Home.class);
                        ((GlobalSocket)getApplication()).setList(my_carriagelist);
                        intent.setClass(MainActivity.this, Home.class);
                        startActivity(intent);
                        Log.d("TAG",my_carriage.getName()+my_carriage.getOwner());
                    }
                    catch(Exception e){
                        Log.d("TAG",e.getMessage());
                    }

                }
                if(result==false){
                    Message msg=Message.obtain();
                    msg.what=333;
                    handler.sendMessage(msg);
                }

                //output.close();

            } catch (IOException e) {
                Log.d("TAG", e.getMessage());
            }
        }
    }

        public void showStuff(View view) {
            LoginThread login = new LoginThread(100);
            Log.d("TAG", "begin to chat");
            login.start();

        }
    }





