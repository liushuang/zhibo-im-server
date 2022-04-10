package com.zhibo.zhiboimserver.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ChatMessage {
    private long id;
    private Integer anchorId;
    private Integer mcnId;
    private Integer userId;
    private String message;
    private Integer type;// 0: 主播发起的 1: MCN 发起的 2： 法律咨询
    private Integer senderType;// 0: 主播 1：MCN 3：管理员
    private Integer senderId;
    private boolean broadcast;
    private Date createdTime;
}
