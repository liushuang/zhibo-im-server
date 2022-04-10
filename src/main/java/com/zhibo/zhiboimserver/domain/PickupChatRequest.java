package com.zhibo.zhiboimserver.domain;

import lombok.Data;

@Data
public class PickupChatRequest {
    private Integer anchorId;
    private Integer mcnId;
    private int type;
    private String token;
}
