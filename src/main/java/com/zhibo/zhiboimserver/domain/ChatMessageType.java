package com.zhibo.zhiboimserver.domain;

/**
 * 聊天类型
 */
public interface ChatMessageType {
    /**
     * 主播
     */
    int ANCHOR = 0;
    /**
     * mcn公司
     */
    int MCN = 1;
    /**
     * 法律咨询
     */
    int LAW = 2;

    /**
     * 后台管理用户
     */
    int USER = 3;
}
