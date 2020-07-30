package com.github.fanzezhen.common.exception.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResponseProperty {
    @Value("${response.json.flag}")
    public boolean jsonFlag;
}
