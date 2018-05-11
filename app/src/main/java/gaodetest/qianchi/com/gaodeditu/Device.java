package gaodetest.qianchi.com.gaodeditu;

/**
 *设备实体类
 */

public class Device {
    private  int  Id;
    private  int  DeviceNum;
    private  int  SerialNum;
    private  int  SimNum;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getDeviceNum() {
        return DeviceNum;
    }

    public void setDeviceNum(int deviceNum) {
        DeviceNum = deviceNum;
    }

    public int getSerialNum() {
        return SerialNum;
    }

    public void setSerialNum(int serialNum) {
        SerialNum = serialNum;
    }

    public int getSimNum() {
        return SimNum;
    }

    public void setSimNum(int simNum) {
        SimNum = simNum;
    }

    @Override
    public String toString() {
        return "Device{" +
                "Id=" + Id +
                ", DeviceNum=" + DeviceNum +
                ", SerialNum=" + SerialNum +
                ", SimNum=" + SimNum +
                '}';
    }
}
