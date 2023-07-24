package com.github.fanzezhen.common.gateway.core.support;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Thomason
 * @version 1.0
 * @since 11-10-19 上午12:10
 */

public class SerializeUtils {
    private static final Logger logger = LoggerFactory.getLogger(SerializeUtils.class);
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        SerializationConfig serializationConfig = MAPPER.getSerializationConfig();
        serializationConfig = serializationConfig.with(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        MAPPER.setConfig(serializationConfig);
        // 忽略空值输出
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DeserializationConfig deserializationConfig = MAPPER.getDeserializationConfig();
        deserializationConfig = deserializationConfig.with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setConfig(deserializationConfig);
    }

    public static String toJson(Object object, boolean ignoreNull) {
        return toJson(object);
    }

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    public static byte[] toJsonBytes(Object object) {
        try {
            return MAPPER.writeValueAsString(object).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return new byte[]{};
    }

    public static <X> X fromJson(String jsonStr, Class<X> x) {
        try {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            return MAPPER.readValue(jsonStr, x);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <X> X fromJson(String jsonStr, TypeReference<X> valueTypeRef) {
        try {
            if (StrUtil.isBlank(jsonStr)) {
                return null;
            }
            return MAPPER.readValue(jsonStr, valueTypeRef);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean canSerialize(Class<?> type) {
        return MAPPER.canSerialize(type);
    }

    public boolean canDeserialize(JavaType type) {
        return MAPPER.canDeserialize(type);
    }
}

