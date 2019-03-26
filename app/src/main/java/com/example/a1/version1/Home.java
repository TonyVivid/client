package com.example.a1.version1;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import trans_data.carriage;
import trans_data.carriageList;
import trans_data.chatInfo;
import trans_data.chatInfoList;
import trans_data.picture;

public class Home extends AppCompatActivity {
    chatadapter mychatadapter;
    stuffAdapter mystuffadapter;
    Handler myhandler;
    private TabHost tabHost;
    private List<chatInfo> infoarray=new ArrayList<>();
    private View home;
    private  View mine;
    private  View upload;
    private int messagesign=0; //判断是否有消息，有则为1，无则为0
    List<View> views = new ArrayList<>();
    private Intent intentMine=null;
    private int clickimg=0;//check which ImageView is clicked
    private int uploadmsg=0;//check whether it's time to upload info;
    private List<String> piclist =new ArrayList<>();
    private String [] picarray=new String[6];
    @Override
    protected void onPause() {
        if(gethomebackMessage!=null && gethomebackMessage.getStatus() == AsyncTask.Status.RUNNING){
            gethomebackMessage.cancel(true);
        }
        super.onPause();
    }
    @Override
protected  void onResume(){
        super.onResume();

    }
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        intentMine=new Intent();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
         home=layoutInflater.inflate(R.layout.activity_stuff, tabHost.getTabContentView());
         mine=layoutInflater.inflate(R.layout.activity_mine, tabHost.getTabContentView());
         upload=layoutInflater.inflate(R.layout.upload, tabHost.getTabContentView());
        tabHost.addTab(tabHost.newTabSpec("").setIndicator("Home").setContent(R.id.stuff));
        tabHost.addTab(tabHost.newTabSpec("").setIndicator("Mine").setContent(R.id.mine));
        tabHost.addTab(tabHost.newTabSpec("").setIndicator("Upload").setContent(R.id.upload));
        initHome(home);

         myhandler =new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==555)
                {
                    Log.d("TAG","得到msg555");
                    adjustInfoArray1(infoarray);
                    initchat(mine);
                    gethomebackMessage.execute();
                }
                if(msg.what==556){
                    adjustInfoArray1(infoarray);
                    mychatadapter.notifyDataSetChanged();
                }
                if(msg.what==557){
                    new UploadStuff().start();
                }
                if(msg.what==558){
                    Log.d("TAG","重新初始化货物列表");
                    initHome(home);
                }
                if(msg.what==559){
                    Toast.makeText(getApplicationContext(),"the input is empty",Toast.LENGTH_SHORT).show();
                }
                if(msg.what==560){
                    EditText txt_name= (EditText) findViewById(R.id.edt_name);
                    EditText txt_price=(EditText)findViewById(R.id.edt_price);
                    EditText txt_info=(EditText)findViewById(R.id.edt_info);
                    txt_name.setText("");
                    txt_info.setText("");
                    txt_price.setText("");
                    for(int i=0;i<picarray.length;i++){
                        picarray[i]=null;
                    }
                    ImageView img1=findViewById(R.id.img1);
                    ImageView img2=findViewById(R.id.img2);
                    ImageView img3=findViewById(R.id.img3);
                    ImageView img4=findViewById(R.id.img4);
                    ImageView img5=findViewById(R.id.img5);
                    ImageView img6=findViewById(R.id.img6);
                    img1.setImageBitmap(null);
                    img2.setImageBitmap(null);
                    img3.setImageBitmap(null);
                    img4.setImageBitmap(null);
                    img5.setImageBitmap(null);
                    img6.setImageBitmap(null);
                }

            }
        };
        new historythread().start();



    }
    class UploadStuff extends  Thread{
        public void run() {
            DataOutputStream out=((GlobalSocket)getApplication()).getOut();
            ObjectOutputStream objout=((GlobalSocket)getApplication()).getObjout();
            carriage mycarriage= new carriage();
            EditText txt_name= (EditText) findViewById(R.id.edt_name);
            EditText txt_price=(EditText)findViewById(R.id.edt_price);
            EditText txt_info=(EditText)findViewById(R.id.edt_info);
            String name=txt_name.getText().toString();
            String tempprice=txt_price.getText().toString();
            String info=txt_info.getText().toString();
            String owner=((GlobalSocket)getApplication()).getName();
            Log.d("TAG",name);
            if(name.equals("")||info.equals("")||tempprice.equals("")||picarray==null){
                Message msg=Message.obtain();
                msg.what=559;
                myhandler.sendMessage(msg);
                return;
            }
            else {
                List<picture> pictureList = new ArrayList<>();
                for (int i = 0; i < picarray.length; i++) {
                    picture pic = new picture();
                    if (picarray[i] != null) {
                        byte[] b = setData(picarray[i]);
                        pic.setPicData(b);
                        pic.setPicSize(b.length);
                        pic.setType(".jpg");
                        pic.setPicName(name);
                        pictureList.add(pic);
                    }
                }
                int price = Integer.valueOf(tempprice);
                mycarriage.setName(name);
                mycarriage.setNumber(owner);
                mycarriage.setOwner(owner);
                mycarriage.setPicture(pictureList);
                mycarriage.setPrice(price);
                mycarriage.setText(info);
                try {
                    out.writeInt(36);
                    objout.writeObject(mycarriage);
                    Message msg=Message.obtain();
                    msg.what=560;
                    myhandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    List<chatInfo> adjustInfoArray(List<chatInfo> list){
        // Iterator<chatInfo> iter = list.iterator();
        HashMap<String,String> tempmap=new HashMap<String,String>();
        List<chatInfo> mylist=new ArrayList<>();
        chatInfo info;
        for(int i=0;i< list.size();i++){
            info=list.get(i);
            String message=tempmap.get(info.getSender());
            if(message!=null){
                message=message+"\n"+info.getInfo();
                tempmap.put(info.getSender(),message);
            }
            else{
                tempmap.put(info.getSender(),info.getInfo());
            }
        }

        for(Map.Entry<String,String> entry : tempmap.entrySet()){
            chatInfo temp=new chatInfo();
            temp.setInfo(entry.getValue());
            Log.d("TAG",temp.getInfo());
            temp.setReceiver(((GlobalSocket) getApplication()).getName());
            temp.setSender(entry.getKey());
            mylist.add(temp);
        }
        return mylist;
    }
   void adjustInfoArray1(List<chatInfo> list){
        // Iterator<chatInfo> iter = list.iterator();
        HashMap<String,String> tempmap=new HashMap<String,String>();
        List<chatInfo> mylist=new ArrayList<>();
        chatInfo info;
        for(int i=0;i< list.size();i++){
            info=list.get(i);
            String message=tempmap.get(info.getSender());
            if(message!=null){
                message=message+"\n"+info.getInfo();
                tempmap.put(info.getSender(),message);
            }
            else{
                tempmap.put(info.getSender(),info.getInfo());
            }
        }

        for(Map.Entry<String,String> entry : tempmap.entrySet()){
            chatInfo temp=new chatInfo();
            temp.setInfo(entry.getValue());
            Log.d("TAG",temp.getInfo());
            temp.setReceiver(((GlobalSocket) getApplication()).getName());
            temp.setSender(entry.getKey());
            mylist.add(temp);
        }
       infoarray=mylist;
    }
    void initHome(View view){
        //final carriageList list=((GlobalSocket)getApplication()).getList();
         mystuffadapter=new stuffAdapter(this,R.layout.stuff_item,((GlobalSocket)getApplication()).getList().getList());
        ListView listview=(ListView)view.findViewById(R.id.list);
        listview.setAdapter(mystuffadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.setClass(Home.this,StuffInfo.class);
                carriageList list=((GlobalSocket)getApplication()).getList();
                carriage mycarriage=list.getList().get(position);
                //intent.putExtra("stuffinfo",mycarriage);
                intent.putExtra("stuffinfo",position);
                startActivity(intent);
            }
        });
    }
    class historythread extends  Thread{
        int type=0;

        chatInfoList clist=null;
        public void run() {
            Socket socket = ((GlobalSocket) getApplication()).getSocket();
            DataInputStream in = ((GlobalSocket) getApplication()).getIn();
            DataOutputStream out = ((GlobalSocket) getApplication()).getOut();
            ObjectOutputStream objout = ((GlobalSocket) getApplication()).getObjout();
            ObjectInputStream objin = ((GlobalSocket) getApplication()).getObjin();
            try {
                String name = ((GlobalSocket) getApplication()).getName();
                out.writeInt(35);
                out.writeUTF(name);
                type= in.readInt();
                Log.d("TAG","the message type:"+String.valueOf(type));
                if(type==446){
                    messagesign=1;
                    Log.d("TAg","here is 446 用户请求历史离线信息");
                    clist=(chatInfoList)objin.readObject();
                    infoarray=clist.getList();
                    Log.d("TAg","here is 446 用户得到了历史离线信息");
                    Message msg=Message.obtain();
                    msg.what=555;
                    myhandler.sendMessage(msg);
                    Log.d("TAg","here is 446 用户得到了历史离线信息");
                }
                if(type==447){
                    Message msg=Message.obtain();
                    msg.what=555;
                    myhandler.sendMessage(msg);
                    messagesign=0;
                    Log.d("TAG","there is no offline message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    class stuffAdapter extends ArrayAdapter {
        private final int sourceID;
        public stuffAdapter(Context context, int resourceID, List<carriage> list){
            super(context,resourceID,list);
            this.sourceID=resourceID;
        }
        public View getView(int position,  View convertView, ViewGroup parent) {
            carriage my_carriage=(carriage)getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(sourceID, null);
            ImageView imgView=(ImageView)view.findViewById(R.id.img_stuff);
            TextView txtname=(TextView)view.findViewById(R.id.txt_name);
            TextView txtprice=(TextView)view.findViewById(R.id.txt_price);
            //Log.d("TAG",my_carriage.getOwner());
            txtname.setText(my_carriage.getName());
            String price=String.valueOf(my_carriage.getPrice());
            txtprice.setText(price);
            Bitmap firstphoto= BitmapFactory.decodeByteArray(my_carriage.getPicture().get(0).getPicData(),0,(int)my_carriage.getPicture().get(0).getPicSize());
            imgView.setImageBitmap(firstphoto);
            return view;
        }
    }
    void initchat(View view){
        Log.d("TAG","begin to init mine");
        //final List<chatInfo> list=((GlobalSocket)getApplication()).getInfo();
        //final List<chatInfo> list = ;
         mychatadapter=new chatadapter(this,R.layout.chatinfo_item,infoarray);
        ListView listview=(ListView)view.findViewById(R.id.list_chat);
        listview.setAdapter(mychatadapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.setClass(Home.this,chat.class);
                intent.putExtra("chatinfo",infoarray.get(position));

                startActivity(intent);
            }
        });

    }
    class chatadapter extends  ArrayAdapter{
        private final int sourceID;
        public chatadapter(Context context, int resourceID, List<chatInfo> list){
            super(context,resourceID,list);
            this.sourceID=resourceID;
        }
        public View getView(int position,  View convertView, ViewGroup parent) {
            chatInfo info=(chatInfo)getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(sourceID, null);
            ImageView imgView=(ImageView)view.findViewById(R.id.img_user);
            TextView txtname=(TextView)view.findViewById(R.id.txt_name);
            TextView txtinfo=(TextView)view.findViewById(R.id.txt_info);
            txtname.setText(info.getSender());
            txtinfo.setText(info.getInfo());
            return view;
        }
    }
    AsyncTask<Void,String,Void> gethomebackMessage=new AsyncTask<Void, String, Void>() {

        @Override
        protected Void doInBackground(Void... voids) {

            DataInputStream in= ((GlobalSocket)getApplication()).getIn();
            DataOutputStream out=((GlobalSocket)getApplication()).getOut();
            ObjectInputStream objin=((GlobalSocket)getApplication()).getObjin();
            ObjectOutputStream objout=((GlobalSocket)getApplication()).getObjout();
            int type=0;

            try {
                while(true) {

                    if(isCancelled())
                        return null;
                   type = in.readInt();

                    Log.d("TAG","Asyntask the message type:"+String.valueOf(type));
                    if(type==445){
                        messagesign=1;
                        Log.d("TAg","here is 445 接收方已经连接");
                        chatInfo info=(chatInfo) objin.readObject();
                        infoarray.add(info);
                        Message msg=Message.obtain();
                        msg.what=556;
                        myhandler.sendMessage(msg);
                        type=0;

                    }
                    if(type==123){
                        Log.d("TAG","here is 123 用户接受更新内容");
                        carriageList mycarriagelist=(carriageList)objin.readObject();
                        ((GlobalSocket)getApplication()).setList(mycarriagelist);
                        Message msg=Message.obtain();
                        msg.what=558;
                        myhandler.sendMessage(msg);
                        type=0;
                        Log.d("TAG","receive the latest stuffinfo");
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

    public static byte[] setData(String path) {

        File file = new File(path);
        try {
            InputStream in = new FileInputStream(file);
            byte[] data = new byte[(int) (file.length())];
            in.read(data);
            in.close();
            return data;

        } catch (IOException e) {
            System.out.println("file problem:" + e.getMessage());

            e.printStackTrace();
            return null;

        }
    }



    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }





    public void SelectPhoto1(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("VIEWID",1);
        startActivityForResult(intent,1);
        clickimg=1;
    }
    public void SelectPhoto2(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("VIEWID",1);
        startActivityForResult(intent,1);
        clickimg=2;
    }
    public void SelectPhoto3(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("VIEWID",1);
        startActivityForResult(intent,1);
        clickimg=3;
    }
    public void SelectPhoto4(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("VIEWID",1);
        startActivityForResult(intent,1);
        clickimg=4;
    }
    public void SelectPhoto5(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("VIEWID",1);
        startActivityForResult(intent,1);
        clickimg=5;
    }
    public void SelectPhoto6(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("VIEWID",1);
        startActivityForResult(intent,1);
        clickimg=6;
    }
    public void UploadStuff(View view){
        Message msg=Message.obtain();
        msg.what=557;
        myhandler.sendMessage(msg);
    }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            ImageView imagview=(ImageView) findViewById(R.id.img2);
            String path=getPhotoPathFromContentUri(this,uri);

            if(clickimg==1){

                imagview=(ImageView) findViewById(R.id.img1);
                picarray[0]=path;

            }
            if(clickimg==2){

                imagview=(ImageView) findViewById(R.id.img2);
                picarray[1]=path;
            }
            if(clickimg==3){

                imagview=(ImageView) findViewById(R.id.img3);
                picarray[2]=path;
            }
            if(clickimg==4){

                imagview=(ImageView) findViewById(R.id.img4);
                picarray[3]=path;
            }

            if(clickimg==5){

                imagview=(ImageView) findViewById(R.id.img5);
                picarray[4]=path;
            }

            if(clickimg==6){

                imagview=(ImageView) findViewById(R.id.img6);
                picarray[5]=path;
            }


            byte [] pic =setData(path);
            Bitmap firstphoto= BitmapFactory.decodeByteArray(pic,0,pic.length);
            imagview.setImageBitmap(firstphoto);
            Log.d("TAG",path);
        }
    }
}








