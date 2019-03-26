package trans_data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class carriage implements Serializable {
    private String number; //货物编号
    private String name;    //货物名称
    private String owner;   //货物拥有者
    private int price;      //货物价格
    private String text;   //货物信息
    private List<picture> mypicture; //货物图片
    public void setName(String name) {
        this.name=name;
    }
    public void setOwner(String owner) {
        this.owner=owner;
    }
    public void setPrice(int price) {
        this.price=price;

    }
    public void setText(String text) {
        this.text=text;

    }

    public void setPicture(List<picture> list) {
        this.mypicture=list;
    }
    public void setNumber(String number) {
        this.number=number;
    }
    public String getNumber() {
        return this.number;
    }
    public String  getName(){
        return name;
    }
    public String getOwner(){
        return owner;
    }
    public String getText(){
        return text;
    }
    public int getPrice(){
        return price;
    }
    public List<picture> getPicture(){
        return this.mypicture;
    }
}

