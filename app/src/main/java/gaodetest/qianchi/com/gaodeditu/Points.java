package gaodetest.qianchi.com.gaodeditu;

/**
 * 描述点类
 */


public class Points {
    private int Id  ;
    private double Longitude;//经度
    private double Latitude;//纬度
    private String TimeStamp;//时间
    private int Speed;//瞬时速度

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
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

    public int getSpeed() {
        return Speed;
    }

    public void setSpeed(int speed) {
        Speed = speed;
    }

    @Override
    public String toString() {
        return "Points{" +
                "id=" + Id +
                ", Longitude=" + Longitude +
                ", Latitude=" + Latitude +
                ", TimeStamp='" + TimeStamp + '\'' +
                ", Speed=" + Speed +
                '}';
    }
}
