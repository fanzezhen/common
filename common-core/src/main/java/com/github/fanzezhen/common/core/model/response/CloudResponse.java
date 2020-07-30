package com.github.fanzezhen.common.core.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CloudResponse<T> {
    String code = "200";
    String msg = "success";
    T data;

    public CloudResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CloudResponse(T data) {
        this.data = data;
    }
}
