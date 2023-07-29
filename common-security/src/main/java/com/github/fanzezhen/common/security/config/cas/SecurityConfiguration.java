package com.github.fanzezhen.common.security.config.cas;

import com.github.fanzezhen.common.core.constant.SecurityConstant;
import com.github.fanzezhen.common.security.annotation.SecurityConfig;
import com.github.fanzezhen.common.security.facade.UserDetailsServiceFacade;
import com.github.fanzezhen.common.security.interceptor.CommonFilterSecurityInterceptor;
import com.github.fanzezhen.common.security.SecurityProperty;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author zezhen.fan
 */
@SecurityConfig
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {
    @Resource
    private SecurityProperty securityProperty;

    @Resource
    private DataSource dataSource;

    @Resource
    private CommonFilterSecurityInterceptor commonFilterSecurityInterceptor;

    @Resource
    private UserDetailsServiceFacade userDetailsServiceFacade;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers(SecurityConstant.CSRF_IGNORING_ANT_MATCHERS))
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/user").failureUrl("/login?error"))
                .logout(LogoutConfigurer::permitAll)
                // 配置安全策略
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(SecurityConstant.IGNORING_ANT_MATCHERS).permitAll()
                        .requestMatchers(securityProperty.getIgnoringAntMatchers()).permitAll()
                        .anyRequest().authenticated()
                ).authenticationProvider(casAuthenticationProvider())
                .addFilter(casAuthenticationFilter())
                .addFilterBefore(casLogoutFilter(), LogoutFilter.class)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                .addFilterBefore(commonFilterSecurityInterceptor, FilterSecurityInterceptor.class)
        ;
        return http.build();
    }
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        // 配置CAS服务器URL等信息
        casAuthenticationFilter.setServiceProperties(serviceProperties());
        casAuthenticationFilter.setFilterProcessesUrl(securityProperty.getAppLoginUrl());
        return casAuthenticationFilter;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        return auth.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * tokenRepository.setCreateTableOnStartup(true);
     * create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)
     *
     * @return PersistentTokenRepository
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    /**
     * 认证的入口
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLoginUrl(securityProperty.getServerLoginUrl());
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }
    /**
     * 指定service相关信息
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(securityProperty.getAppUrl() + securityProperty.getAppLoginUrl());
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**
     * 单点登出过滤器
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter.setLogoutCallbackPath(securityProperty.getServerUrl());
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    /**
     * 请求单点退出过滤器
     */
    @Bean
    public LogoutFilter casLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(securityProperty.getServerLogoutUrl(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(securityProperty.getAppLogoutUrl());
        return logoutFilter;
    }

    /**
     * cas 认证 Provider
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsServiceFacade);
        // 这里只是接口类型，实现的接口不一样，都可以的。
        casAuthenticationProvider.setUserDetailsService(userDetailsServiceFacade);
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(new Cas30ServiceTicketValidator(securityProperty.getServerUrl()));
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");
        return casAuthenticationProvider;
    }

}
