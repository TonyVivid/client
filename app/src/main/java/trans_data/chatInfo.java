package trans_data;

import java.io.Serializable;

public class chatInfo extends loginInfo implements Serializable {

    String receiver;
    String sender;
    String info;

    public void setReceiver(String receiver){
        this.receiver=receiver;
    }
    public void setSender(String sender) {
        this.sender=sender;
    }
    public void setInfo(String info){
        this.info=info;
    }
    public String getReceiver(){
        return this.receiver;
    }
    public String getInfo(){
        return this.info;
    }
    public String getSender() {
        return this.sender;
    }
}
