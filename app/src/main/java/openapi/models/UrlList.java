package openapi.models;

import java.util.List;

import cn.net.cc.weibo.json.JsonProvider;

/**
 * Created by chengkai on 2016/10/8.
 */
public class UrlList {

    public List<UrlPojo> urls;

    public static UrlList parse(String source) {
        return JsonProvider.json2pojo(source, UrlList.class);
    }
}
