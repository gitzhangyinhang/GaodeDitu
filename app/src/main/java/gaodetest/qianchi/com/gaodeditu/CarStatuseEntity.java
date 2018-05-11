package gaodetest.qianchi.com.gaodeditu;

/**
 * Created by Administrator on 2018/4/2.
 */

public class CarStatuseEntity {
    private  Car Car;//车辆类
    private  String Status;//状态:离线，停驶，行驶
    private  boolean IsLocated;//是否有定位
    private  int Speed;//实时速度
    private  int Direction;//朝向（可能不准确，暂未使用）
    private  double Longitude;//经度（待修正：离线状态时，显示最后定位的经纬度）
    private  double Latitude;//纬度  经纬度都为0表示没有最后定位数据，不显示
    private  String TimeStamp;//定位UTC时间

    public gaodetest.qianchi.com.gaodeditu.Car getCar() {
        return Car;
    }

    public void setCar(gaodetest.qianchi.com.gaodeditu.Car car) {
        Car = car;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public boolean isLocated() {return IsLocated;}

    public void setLocated(boolean located) {
        IsLocated = located;
    }

    public int getSpeed() {
        return Speed;
    }

    public void setSpeed(int speed) {
        Speed = speed;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "CarStatuseEntity{" +
                "Car=" + Car +
                ", Status='" + Status + '\'' +
                ", IsLocated=" + IsLocated +
                ", Speed=" + Speed +
                ", Direction=" + Direction +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", TimeStamp='" + TimeStamp + '\'' +
                '}';
    }
}
