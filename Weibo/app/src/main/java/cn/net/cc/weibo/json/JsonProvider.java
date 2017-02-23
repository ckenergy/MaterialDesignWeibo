package cn.net.cc.weibo.json;

import com.google.gson.Gson;

/**
 * Created by chengkai on 2016/8/27.
 */
public class JsonProvider {

    /**使用gson解析*/
    public static <T> T json2pojo (String json, Class<T> clazz) {
        return new Gson().fromJson(json, clazz);
    }
}
