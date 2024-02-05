package com.github.fanzezhen.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zezhen.fan
 */
@Slf4j
@Component
public class ExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        // 这里也可以根据需要返回html页面或者json数据
        Map<String, Object> map = new HashMap<>(2, 1);
        map.put("code", 0);
        map.put("msg", "已经另一台机器登录，您被迫下线。" + event.getSessionInformation().getLastRequest());
        event.getResponse().setContentType("application/json;charset=UTF-8");
        event.getResponse().getWriter().write(objectMapper.writeValueAsString(map));
    }
}
