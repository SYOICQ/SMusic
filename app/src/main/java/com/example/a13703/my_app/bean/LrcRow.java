package com.example.a13703.my_app.bean;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13703 on 2019/6/30.
 */

public class LrcRow implements Comparable<LrcRow>,Serializable {

    public String startTime;
    public long time;
    public String content;
    public LrcRow(){}

    public LrcRow(String startTime, long time, String content) {
        this.startTime = startTime;
        this.time = time;
        this.content = content;
    }

    @Override
    public String toString() {
        return "[" + startTime + " ]"  + content;
    }

    public static List<LrcRow> createRows(String standardLrcLine){
        /**
         [01:15.33]我好想你 好想你
         [02:34.14][01:07.00]当你我不小心又想起她
         **/
        try{
            if(standardLrcLine.indexOf("[") != 0 || standardLrcLine.indexOf("]") != 9 ){
                return null;
            }

            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            //歌词内容
            String content = standardLrcLine.substring(lastIndexOfRightBracket + 1, standardLrcLine.length());
            String times = standardLrcLine.substring(0,lastIndexOfRightBracket + 1).replace("[", "-").replace("]", "-");
            //通过 ‘-’ 来拆分字符串
            String arrTimes[] = times.split("-");
            List<LrcRow> listTimes = new ArrayList<>();
            for(String temp : arrTimes){
                if(temp.trim().length() == 0){
                    continue;
                }
                LrcRow lrcRow = new LrcRow(temp, timeConvert(temp), content);
                listTimes.add(lrcRow);
            }
            return listTimes;
        }catch(Exception e){
            Log.e("LrcRow","createRows exception:" + e.getMessage());
            return null;
        }
    }

    private static long timeConvert(String timeString){
        timeString = timeString.replace('.', ':');
        String[] times = timeString.split(":");
        return Integer.valueOf(times[0]) * 60 * 1000 +//分
                Integer.valueOf(times[1]) * 1000 +//秒
                Integer.valueOf(times[2]) ;//毫秒
    }

        @Override
    public int compareTo(LrcRow lrcRow) {
            return (int)(this.time - lrcRow.time);
        }
}
