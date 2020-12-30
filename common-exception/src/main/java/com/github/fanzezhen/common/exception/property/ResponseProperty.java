package com.github.fanzezhen.common.exception.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Getter
@Component
public class ResponseProperty {
    @Value("${response.json.flag:false}")
    private boolean responseJson;
}
