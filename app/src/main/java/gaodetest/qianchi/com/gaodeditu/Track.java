package gaodetest.qianchi.com.gaodeditu;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 轨迹类
 */

public class Track {
    private int Id;
    private String  Begin;//轨迹起始时间（UTC），第一个点时间
    private String  End;///轨迹结束时间（UTC），最后一个点时间
    private int  Length;//长度，单位：米
    private List<Points> Points;//定位点数组

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getBegin() {
        return Begin;
    }

    public void setBegin(String begin) {
        Begin = begin;
    }

    public String getEnd() {
        return End;
    }

    public void setEnd(String end) {
        End = end;
    }

    public int getLength() {
        return Length;
    }

    public void setLength(int length) {
        this.Length = length;
    }

    public List<Points> getPoints() {
        return Points;
    }

    public void setPoints(List<Points> points) {
        this.Points = points;
    }

    @Override
    public String toString() {
        return "track{" +
                "Begin='" + Begin + '\'' +
                ", End='" + End + '\'' +
                ", length=" + Length +
                ", points=" + Points +
                '}';
    }
}
