package trans_data;

import java.io.Serializable;

public class transSign implements Serializable {
    int message;
    public void setMessage(int message){
        this.message=message;
    }
    public int getMessage(){
        return this.message;
    }
}
