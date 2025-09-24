package jymk.stream.entity;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherInfo {
    public WeatherInfo() {
    }

    //    值为0或1
    // 1：成功；0：失败
    public String status;

    //    返回结果总数目
    public String count;
    //    返回的状态信息
    public String info;

    //    返回状态说明,10000代表正确
    public String infocode;
    //    实况天气数据信息
    public List<Lives> lives;

    public List<Forecasts> forecasts;
    public static class Lives {
        public Lives() {
        }

        //    省份名
        public String province;

        //    城市名
        public String city;

        //    区域编码
        public String adcode;
        //    天气现象（汉字描述）
        public String weather;

        //    实时气温，单位：摄氏度
        public String temperature;

        //    风向描述
        public String winddirection;
        //    风力级别，单位：级
        public String windpower;

        //    空气湿度
        public String humidity;
        //    数据发布的时间
        public String reporttime;

        public String temperature_float;

        public String humidity_float;

        @Override
        public String toString() {
            return "Lives{" +
                    "province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", adcode='" + adcode + '\'' +
                    ", weather='" + weather + '\'' +
                    ", temperature='" + temperature + '\'' +
                    ", winddirection='" + winddirection + '\'' +
                    ", windpower='" + windpower + '\'' +
                    ", humidity='" + humidity + '\'' +
                    ", reporttime='" + reporttime + '\'' +
                    ", temperature_float='" + temperature_float + '\'' +
                    ", humidity_float='" + humidity_float + '\'' +
                    '}';
        }
    }


    public static class Forecasts {
        public Forecasts() {
        }

        //    城市名称
        public String city;

        //    城市编码
        public String adcode;

        //    省份名称
        public String province;
        //    预报发布时间
        public String reporttime;
        //    预报数据list结构，元素cast,按顺序为当天、第二天、第三天的预报数据
        public List<Casts> casts;

        @Override
        public String toString() {
            return "Forecasts{" +
                    "city='" + city + '\'' +
                    ", adcode='" + adcode + '\'' +
                    ", province='" + province + '\'' +
                    ", reporttime='" + reporttime + '\'' +
                    ", casts=" + casts +
                    '}';
        }
    }

    public static class Casts {
        public Casts() {
        }

        //    日期
        public String date;
        //    星期几
        public String week;
        //    白天天气现象
        public String dayweather;

        //    晚上天气现象
        public String nightweather;
        //    白天温度
        public String daytemp;
        //    晚上温度
        public String nighttemp;

        //    白天风向
        public String daywind;
        //    晚上风向
        public String nightwind;
        //    白天风力
        public String daypower;
        //    晚上风力
        public String nightpower;

        public String daytemp_float;

        public String nighttemp_float;

        @Override
        public String toString() {
            return "Casts{" +
                    "date='" + date + '\'' +
                    ", week='" + week + '\'' +
                    ", dayweather='" + dayweather + '\'' +
                    ", nightweather='" + nightweather + '\'' +
                    ", daytemp='" + daytemp + '\'' +
                    ", nighttemp='" + nighttemp + '\'' +
                    ", daywind='" + daywind + '\'' +
                    ", nightwind='" + nightwind + '\'' +
                    ", daypower='" + daypower + '\'' +
                    ", nightpower='" + nightpower + '\'' +
                    ", daytemp_float='" + daytemp_float + '\'' +
                    ", nighttemp_float='" + nighttemp_float + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "status='" + status + '\'' +
                ", count='" + count + '\'' +
                ", info='" + info + '\'' +
                ", infocode='" + infocode + '\'' +
                ", lives=" + lives +
                ", forecasts=" + forecasts +
                '}';
    }
}
