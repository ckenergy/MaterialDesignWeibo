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
public class UserList {
    
    /** 微博列表 */
    public ArrayList<User> users;
    public String previous_cursor;
    public String next_cursor;
    public int total_number;

    public static UserList parse(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        
        UserList statuses = JsonProvider.json2pojo(jsonString,UserList.class);
        return statuses;
    }
}
