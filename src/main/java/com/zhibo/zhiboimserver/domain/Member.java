package com.zhibo.zhiboimserver.domain;

import lombok.Data;

@Data
public class Member {
    private Integer id;
    private String username;
    private String headImgUrl;
    private int type;
}
