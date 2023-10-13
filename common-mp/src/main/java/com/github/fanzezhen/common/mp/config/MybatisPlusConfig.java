package com.github.fanzezhen.common.mp.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = {"com.github.fanzezhen.*.mapper*", "${com.github.fanzezhen.common.mp.package:}"})
public class MybatisPlusConfig {
    @Resource
    private CommonCoreProperties commonCoreProperties;
    @Value("${com.github.fanzezhen.common.mp.ignore-tenant-tables:[]}")
    private Set<String> ignoreTenantTables;
    @Value("${com.github.fanzezhen.common.mp.sharding-by-tenant-tables:[]}")
    private Set<String> shardingByTenantTables;

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        String tenantId = SysContextHolder.getTenantId();
        if (commonCoreProperties.isTenantEnabled()) {
            interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
                @Override
                public Expression getTenantId() {
                    return new StringValue(tenantId);
                }
                // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
                @Override
                public boolean ignoreTable(String tableName) {
                    if (ignoreTenantTables == null){
                        return false;
                    }
                    for (String ignoreTenantTable : ignoreTenantTables) {
                        if (tableName.equalsIgnoreCase(ignoreTenantTable)){
                            return true;
                        }
                    }
                    return false;
                }
            }));
        }
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            if (ignoreTenantTables == null){
                return tableName;
            }
            for (String tb : shardingByTenantTables) {
                if (tableName.equalsIgnoreCase(tb)){
                    if (StrUtil.isEmpty(tenantId)){
                        return tableName;
                    }
                    return tableName + StrUtil.UNDERLINE + tenantId;
                }
            }
            return tableName;
        });
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return interceptor;
    }

    /**
     * 用于序列化枚举值为数据库存储值
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    }

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 序列化枚举值为数据库存储值
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteEnumUsingToString);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }

    // TODO 需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
}
