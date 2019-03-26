package trans_data;

import java.io.Serializable;

public class loginInfo implements Serializable{

    private int MSG_INFO;
    private String number;
    private String password;
    public void setMsgInfo(int msg) {
        this.MSG_INFO=msg;

    }

    public void setNumber(String number) {
        this.number=number;
    }
    public void setPassword(String password) {
        this.password=password;
    }
    public int getMsgInfo() {
        return MSG_INFO;
    }
    public String getNumber() {
        return number;
    }
    public String getPassword() {
        return password;
    }

}
