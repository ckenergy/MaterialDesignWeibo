package cn.net.cc.weibo.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengkai on 2016/9/1.
 */
public class StatuUrlsConverter implements PropertyConverter<ArrayList<String>, String> {
    @Override
    public ArrayList<String> convertToEntityProperty(String databaseValue) {
        Type type = new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(databaseValue,type);
    }

    @Override
    public String convertToDatabaseValue(ArrayList<String> entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}
