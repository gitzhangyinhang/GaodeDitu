package gaodetest.qianchi.com.gaodeditu;

/**
 * Created by Administrator on 2018/4/2.
 */

public class Car {
private Device Device;//设备类
private Driver Driver; //驾驶员类
private int  Id; // 车辆ID
private String PlateID;//车牌号
private int TotalKm;//总里程
private String Model;//车型

    public gaodetest.qianchi.com.gaodeditu.Device getDevice() {
        return Device;
    }

    public void setDevice(gaodetest.qianchi.com.gaodeditu.Device device) {
        Device = device;
    }

    public gaodetest.qianchi.com.gaodeditu.Driver getDriver() {
        return Driver;
    }

    public void setDriver(gaodetest.qianchi.com.gaodeditu.Driver driver) {
        Driver = driver;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getPlateID() {
        return PlateID;
    }

    public void setPlateID(String plateID) {
        PlateID = plateID;
    }

    public int getTotalKm() {
        return TotalKm;
    }

    public void setTotalKm(int totalKm) {
        TotalKm = totalKm;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    @Override
    public String toString() {
        return "Car{" +
                "Device=" + Device +
                ", Driver=" + Driver +
                ", Id=" + Id +
                ", PlateID='" + PlateID + '\'' +
                ", TotalKm=" + TotalKm +
                ", Model='" + Model + '\'' +
                '}';
    }
}
