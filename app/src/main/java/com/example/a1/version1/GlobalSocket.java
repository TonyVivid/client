package com.example.a1.version1;

import android.app.Application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import trans_data.carriage;
import trans_data.carriageList;
import trans_data.chatInfo;

public class GlobalSocket  extends Application {

    private int port=0;
    private InetAddress address=null;
    private Socket socket=null;
    private DataInputStream in=null;
    private DataOutputStream out=null;
    private ObjectInputStream objin=null;
    private ObjectOutputStream objout=null;
    private carriageList list=null;
    private List<chatInfo> info=null;
    private String name=null;
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return  this.name;
    }
    public  void setInfo(List<chatInfo> info){
        this.info=info;
    }
    public List<chatInfo> getInfo(){
        return this.info;
    }
    public void setList(carriageList list){
        this.list=list;

    }
    public void setPort(int i){
        this.port=i;
    }
    public Socket getSocket(){
        return this.socket;

    }
    public DataInputStream getIn(){
        return  this.in;
    }
    public  DataOutputStream getOut(){
        return  this.out;
    }
    public  ObjectInputStream getObjin(){
        return  this.objin;
    }
    public ObjectOutputStream getObjout(){
        return this.objout;
    }
    public void setIn(DataInputStream in){
        this.in=in;
    }
    public  void setOut(DataOutputStream out){
        this.out=out;
    }
    public void setObjin(ObjectInputStream objin){
        this.objin=objin;
    }
    public void setObjout(ObjectOutputStream objout){
        this.objout=objout;
    }
    public void setSocket(Socket socket){
        this.socket=socket;
    }
    public void setAddress(InetAddress address) {
        this.address = address;
    }
    public int getPort(){
        return this.port;
    }
    public InetAddress getAddress(){
        return this.address;
    }
    public carriageList getList(){return  this.list;}
}
