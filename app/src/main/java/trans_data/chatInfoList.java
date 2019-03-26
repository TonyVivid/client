package trans_data;

import java.io.Serializable;
import java.util.List;

public class chatInfoList implements Serializable {
    List<chatInfo> list;
    public void setList(List<chatInfo> list) {
        this.list=list;
    }
    public List<chatInfo> getList(){
        return this.list;
    }

}
