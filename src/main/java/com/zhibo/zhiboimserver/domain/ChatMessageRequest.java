package com.zhibo.zhiboimserver.domain;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Integer type;
    private Integer anchorId;
    private Integer mcnId;
    private Integer pageSize = 20;
    private Integer offset = Integer.MAX_VALUE;
    private String token;
}
