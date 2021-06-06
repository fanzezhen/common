package com.github.fanzezhen.common.gateway.core.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Thomason
 * @version 1.0
 * @since 11-10-19 上午12:10
 */

@SuppressWarnings({"unchecked"})
public class SerializeUtils {
	private static Logger logger = LoggerFactory.getLogger(SerializeUtils.class);
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		SerializationConfig serializationConfig = mapper.getSerializationConfig();
		serializationConfig = serializationConfig.with(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
				.without(SerializationFeature.WRITE_NULL_MAP_VALUES);
		mapper.setConfig(serializationConfig);
		// 忽略空值输出
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
		deserializationConfig = deserializationConfig.with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
				.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setConfig(deserializationConfig);
	}

	public static String toJson(Object object, boolean ignoreNull) {
		return toJson(object);
	}

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	public static byte[] toJsonBytes(Object object) {
		try {
			return mapper.writeValueAsString(object).getBytes(Charset.forName("UTF-8"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new byte[]{};
	}

	public static <X> X fromJson(String jsonStr, Class<X> x) {
		try {
			if (StringUtils.isBlank(jsonStr)) {
				return null;
			}
			return mapper.readValue(jsonStr, x);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static <X> X fromJson(String jsonStr, TypeReference<X> valueTypeRef) {
		try {
			if (StringUtils.isBlank(jsonStr)) {
				return null;
			}
			return mapper.readValue(jsonStr, valueTypeRef);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static boolean canSerialize(Class<?> type) {
		return mapper.canSerialize(type);
	}

	public boolean canDeserialize(JavaType type) {
		return mapper.canDeserialize(type);
	}
}

