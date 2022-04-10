package com.zhibo.zhiboimserver.domain;

import lombok.Data;

@Data
public class ChatWithUserInfo extends Chat{
    private String headImgUrl;
    private String username;
}
