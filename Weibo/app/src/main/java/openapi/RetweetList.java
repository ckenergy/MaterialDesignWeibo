/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package openapi;

import android.text.TextUtils;

import java.util.ArrayList;

import cn.net.cc.weibo.json.JsonProvider;
import openapi.models.Status;

/**
 * 微博列表结构。
 *
 * @author SINA
 * @see <a href="http://t.cn/zjM1a2W">常见返回对象数据结构</a>
 * @since 2013-11-22
 */
public class RetweetList {

    public ArrayList<Status> reposts = new ArrayList<Status>();
    public boolean hasvisible;
    public String previous_cursor;
    public String next_cursor;
    public int total_number;
    public int interval;

    public static RetweetList parse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        RetweetList statuses = JsonProvider.json2pojo(jsonString, RetweetList.class);

        //对status中的本地私有字段进行赋值
        /*for (Status status : statuses.reposts) {
            //服务器并没有返回我们单张图片的随机尺寸，这里我们手动需要随机赋值
            FillContentHelper.setSingleImgSizeType(status);
            //提取微博来源的关键字
            FillContentHelper.setSource(status);
            //设置三种类型图片的url地址
            FillContentHelper.setImgUrl(status);

            if (status.retweeted_status != null) {
                //服务器并没有返回我们单张图片的随机尺寸，这里我们手动需要随机赋值
                FillContentHelper.setSingleImgSizeType(status.retweeted_status);
                //提取微博来源的关键字
                FillContentHelper.setSource(status.retweeted_status);
                //设置三种类型图片的url地址
                FillContentHelper.setImgUrl(status.retweeted_status);
            }
        }*/
        return statuses;
    }

}
