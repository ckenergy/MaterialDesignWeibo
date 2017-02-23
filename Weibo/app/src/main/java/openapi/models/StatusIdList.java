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

package openapi.models;

import android.text.TextUtils;

import java.util.ArrayList;

import cn.net.cc.weibo.json.JsonProvider;

/**
 * 微博列表结构。
 * @see <a href="http://t.cn/zjM1a2W">常见返回对象数据结构</a>
 * 
 * @author SINA
 * @since 2013-11-22
 */
public class StatusIdList {
    
    /** 微博列表 */
    public ArrayList<String> statuses;
    public String previous_cursor;
    public String next_cursor;
    public int total_number;

    public static StatusIdList parse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        
        StatusIdList statuses = JsonProvider.json2pojo(jsonString,StatusIdList.class);
        /*try {
            JSONObject jsonObject = new JSONObject(jsonString);
            statuses.hasvisible      = jsonObject.optBoolean("hasvisible", false);
            statuses.previous_cursor = jsonObject.optString("previous_cursor", "0");
            statuses.next_cursor     = jsonObject.optString("next_cursor", "0");
            statuses.total_number    = jsonObject.optInt("total_number", 0);
            
            JSONArray jsonArray      = jsonObject.optJSONArray("statuses");
            if (jsonArray != null && jsonArray.length() > 0) {
                int length = jsonArray.length();
                statuses.statusList = new ArrayList<Status>(length);
                for (int ix = 0; ix < length; ix++) {
                    statuses.statusList.add(Status.parse(jsonArray.getJSONObject(ix)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        
        return statuses;
    }
}
