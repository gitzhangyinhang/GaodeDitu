package gaodetest.qianchi.com.gaodeditu;

/**
 * Created by Administrator on 2018/4/2.
 */

public class Driver {
    private int Id;
    private String Name;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                '}';
    }
}
