package trans_data;

import java.io.Serializable;
import java.util.List;

public class carriageList implements Serializable{

    private List<carriage> list;
    public void setList(List<carriage> list) {
        this.list=list;
    }
    public List <carriage>  getList() {
        return this.list;
    }
}
