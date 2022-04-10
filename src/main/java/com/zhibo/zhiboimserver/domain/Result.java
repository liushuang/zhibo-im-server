package com.zhibo.zhiboimserver.domain;

import lombok.Data;

@Data
public class Result<T> {
    public static int CODE_SUCCESS = 0;
    public static int CODE_400 = 400;

    public Result(T result) {
        this.result = result;
    }

    public Result(T result, int code) {
        this.result = result;
        this.code = code;
    }

    private int code = CODE_SUCCESS;
    private T result;
}
