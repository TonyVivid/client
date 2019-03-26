package com.example.a1.version1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import trans_data.carriage;
import trans_data.picture;

public class StuffInfo extends AppCompatActivity {
    carriage my_carriage=new carriage();
    List<ImageView> imageViewList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuff_info);
         //my_carriage=(carriage) getIntent().getSerializableExtra("stuffinfo");
        int position =getIntent().getIntExtra("stuffinfo",0);
        my_carriage=((GlobalSocket)getApplication()).getList().getList().get(position);
        int picsize=my_carriage.getPicture().size();
         for(int i=0;i<picsize;i++){
             View view=LayoutInflater.from(this).inflate(R.layout.img_item,null);
             ImageView imgview=(ImageView)view.findViewById(R.id.img);
             int length=(int)my_carriage.getPicture().get(i).getPicSize();
             Bitmap photo = BitmapFactory.decodeByteArray(my_carriage.getPicture().get(i).getPicData(), 0, length);
             imgview.setImageBitmap(photo);
             imageViewList.add(imgview);
         }

        Init(my_carriage);
    }
    public void Init(carriage c){
        String info;
        TextView txt_intro=(TextView) findViewById(R.id.txt_intro);
        info=c.getName()+"\n"+"Price:"+String.valueOf(c.getPrice())+"RMB"+"\n"+"Introduction:"+c.getText();
        txt_intro.setText(info);
        Log.d("TAG",String.valueOf(c.getPicture().size()));
        ViewPager pager= (ViewPager)findViewById(R.id.view_page);
        mypageAdapter adapter=new mypageAdapter(c,StuffInfo.this);
        pager.setAdapter(adapter);

    }
    public void btn_contact(View view){
        Intent intent=new Intent();
        intent.putExtra("owner",my_carriage.getOwner());
        intent.setClass(StuffInfo.this,chat.class);
        startActivity(intent);
    }
    class mypageAdapter extends  PagerAdapter{

        carriage my_carriage;
        Context context;
        mypageAdapter(carriage c,Context context){
            this.context= context;
            this.my_carriage=c;
        }
        public int getCount() {

            return my_carriage.getPicture().size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

               List<picture> list=my_carriage.getPicture();
            int newPosition = position % imageViewList.size();
            ImageView imageView = imageViewList.get(newPosition);

            //View view=LayoutInflater.from(context).inflate(R.layout.img_item,null);
               // ImageView imgview=(ImageView)view.findViewById(R.id.img);
               // Bitmap photo = BitmapFactory.decodeByteArray(list.get(position).getPicData(), 0, (int) list.get(position).getPicSize());
                //imgview.setImageBitmap(photo);
               // container.addView(imgview);
                //return  imgview;
                container.addView(imageView);
                return imageView;

        }
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View)object);
        }
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }


    }
}
