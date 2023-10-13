package com.github.fanzezhen.common.mp.handler;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonArrTypeHandler extends AbstractFastjsonTypeHandler<JSONArray> {

    public JsonArrTypeHandler() {
        super(JSONArray.class);
    }
}