package com.zhibo.zhiboimserver.domain;

import java.util.Date;

import lombok.Data;

@Data
public class Chat {
    private long id;
    private Integer anchorId;
    private Integer mcnId;
    private Integer userId;
    private Integer starterId;
    private int type;
    private int unreadCount;
    private boolean picked;
    private Date pickTime;
    private Date createdTime;
    private Date updatedTime;
}
