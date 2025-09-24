package jymk.stream.entity;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CityCode {
    public CityCode() {
    }

    @SerializedName("Sheet1")
    public List<CitySheet> Sheet1 = new ArrayList<>();
    public static class CitySheet {
        public CitySheet() {
        }

        public String 中文名;
        public String adcode;
        public String citycode;

        @Override
        public String toString() {
            return "CitySheet{" +
                    "中文名='" + 中文名 + '\'' +
                    ", adcode='" + adcode + '\'' +
                    ", citycode='" + citycode + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CityCode{" +
                "Sheet1=" + Sheet1 +
                '}';
    }
}
