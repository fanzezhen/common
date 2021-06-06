package com.github.fanzezhen.common.gateway.core.filter.auth.factory.csp;

import com.github.fanzezhen.common.gateway.core.constant.SystemConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

/**
 * @author zezhen.fan
 */
public enum CspTokenTransferMode {
    /**
     * cookie
     */
    cookie {
        @Override
        public Optional<String> getToken(ServerHttpRequest request) {
            if (logger.isDebugEnabled()) {
                logger.debug("TokenTransferMode use cookie");
            }
            List<HttpCookie> tokens = request.getCookies().get("token");
            if (tokens != null && tokens.size() > 1) {
                logger.warn("found more than one tokens: {}", tokens.size());
            }
            return CollectionUtils.isEmpty(tokens) ? Optional.empty() : Optional.of(tokens.get(0).getValue());
        }
    },
    /**
     * parameter
     */
    parameter {
        @Override
        public Optional<String> getToken(ServerHttpRequest request) {
            if (logger.isDebugEnabled()) {
                logger.debug("TokenTransferMode use parameter");
            }
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            String first = queryParams.getFirst(SystemConstant.tokenName());
            return StringUtils.isNotBlank(first) ? Optional.of(first) : Optional.empty();
        }
    },
    /**
     * header
     */
    header {
        @Override
        public Optional<String> getToken(ServerHttpRequest request) {
            if (logger.isDebugEnabled()) {
                logger.debug("TokenTransferMode use header");
            }
            List<String> tokens = request.getHeaders().get("COMMON-Header-Token");
            if (tokens != null && tokens.size() > 1) {
                logger.warn("found more than one tokens: {}", tokens.size());
            }
            return CollectionUtils.isEmpty(tokens) ? Optional.empty() : Optional.of(tokens.get(0));
        }
    },
    /**
     * misc
     */
    misc {
        @Override
        public Optional<String> getToken(ServerHttpRequest request) {
            if (logger.isDebugEnabled()) {
                logger.debug("TokenTransferMode use misc");
            }
            Optional<String> token = cookie.getToken(request);
            return token.isPresent() ? token : header.getToken(request);
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(CspTokenTransferMode.class);

    /**
     * 获取sessionId
     *
     * @param request httpRequest
     * @return sessionId
     */
    public abstract Optional<String> getToken(ServerHttpRequest request);
}
