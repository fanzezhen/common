package com.github.fanzezhen.common.gateway.endpoint.auth;

import com.github.fanzezhen.common.core.constant.SysConstant;
import com.github.fanzezhen.common.gateway.core.constant.CommonGatewayConstant;
import com.github.fanzezhen.common.gateway.core.support.StringUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Configuration
public class ActuatorSecurity {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SecurityContextRepository securityContextRepository;

    @Bean
    @ConditionalOnBean(ServerHttpSecurity.class)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http.authenticationManager(authenticationManager);
        http.securityContextRepository(securityContextRepository);

        ServerHttpSecurity.AuthorizeExchangeSpec authenticated = http.authorizeExchange().matchers(EndpointRequest.toAnyEndpoint())
                .authenticated();
        return authenticated
                .and().authorizeExchange().anyExchange().permitAll().and().build();
    }

    @Component
    public static class AuthenticationManager implements ReactiveAuthenticationManager {

        @Value("${com.github.fanzezhen.common.gateway.security.token:}")
        private String validToken;

        @Override
        public Mono<Authentication> authenticate(Authentication authentication) {
            // JwtAuthenticationToken is my custom token.
            if (authentication instanceof SimpleAuthentication simpleAuthentication) {
                if (StringUtil.isNotBlank(simpleAuthentication.getToken())) {
                    authentication.setAuthenticated(simpleAuthentication.getToken().equals(validToken));
                }
            }
            return Mono.just(authentication);
        }
    }

    @Component
    public static class SecurityContextRepository implements ServerSecurityContextRepository {

        @Value("${com.github.fanzezhen.common.gateway.security.token:}")
        private String validToken;

        @Override
        public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
            // Don't know yet where this is for.
            return null;
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {

            List<String> tokenList = serverWebExchange.getRequest().getHeaders().get(SysConstant.HEADER_TOKEN);
            SimpleAuthentication authentication = new SimpleAuthentication();
            if (tokenList != null && tokenList.size() > 0) {
                authentication.setToken(tokenList.get(0));
                boolean authed = validToken.equals(tokenList.get(0));
                authentication.setAuthenticated(authed);
            }
            return Mono.just(new SecurityContextImpl(authentication));
        }
    }

    public static class SimpleAuthentication implements Authentication {

        private boolean isAuthenticated = false;

        private String token;


        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            throw new NotImplementedException();
        }

        @Override
        public Object getCredentials() {
            throw new NotImplementedException();
        }

        @Override
        public Object getDetails() {
            throw new NotImplementedException();
        }

        @Override
        public Object getPrincipal() {
            throw new NotImplementedException();
        }

        @Override
        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {
            isAuthenticated = b;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String getName() {
            return "SimpleAuthentication";
        }
    }
}
