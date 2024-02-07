package com.github.fanzezhen.common.magic.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.web.MagicResourceController;

/**
 * 配置
 *
 * @author fanzezhen
 * @createTime 2024/2/5 17:35
 * @since 1.0.0
 */
@Configuration
public class MagicApiConfig {
    @Bean
    @ConditionalOnMissingBean
    public MagicResourceController magicResourceController(@Autowired MagicConfiguration magicConfiguration){
        return new MagicResourceController(magicConfiguration);
    }
}
