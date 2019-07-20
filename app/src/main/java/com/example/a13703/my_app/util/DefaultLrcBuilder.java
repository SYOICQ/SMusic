package com.example.a13703.my_app.util;

import android.util.Log;

import com.example.a13703.my_app.Inter_kou.ILrcBuilder;
import com.example.a13703.my_app.bean.LrcRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 13703 on 2019/6/30.
 */

public class DefaultLrcBuilder implements ILrcBuilder {
    private final String TAG = "DefaultLrcBuilder";
    @Override
    public List<LrcRow> getLrcRows(String rawLrc) {
        if(rawLrc==null||rawLrc.length() == 0){
            Log.e(TAG,"getLrcRows rawLrc null or empty");
            return null;
        }
        StringReader reader = new StringReader(rawLrc);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LrcRow> rows = new ArrayList<>();
        try{
            do {
                line = br.readLine();
                if (line != null && line.length() > 0) {
                    List<LrcRow> lrcRows = LrcRow.createRows(line);
                    if (lrcRows != null && lrcRows.size() > 0) {
                        for (LrcRow row : lrcRows) {
                            rows.add(row);
                        }
                    }
                }
            }while(line !=null);
            if(rows.size()>0){
                //排序
                Collections.sort(rows);
            }
        }catch(Exception e){
            Log.e(TAG,"parse exceptioned:" + e.getMessage());
            return null;
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
    }
}
