package trans_data;

import java.io.Serializable;

public class picture implements Serializable{
    private String pic_name;
    private long pic_size;
    private byte[] pic_data;
    private String type;
    public void setType(String type) {
        this.type=type;
    }
    public void setPicName(String name) {
        this.pic_name=name;

    }
    public void setPicSize(long size) {

        this.pic_size=size;

    }
    public void setPicData(byte []pic) {
        this.pic_data=new byte[pic.length];
        for(int i=0;i<pic.length;i++) {
            this.pic_data[i]=pic[i];
        }
    }
    public String getPicName() {
        return  this.pic_name;
    }
    public long getPicSize() {
        return this.pic_size;
    }
    public byte[] getPicData() {
        return this.pic_data;
    }
    public String getType() {
        return this.type;
    }

}