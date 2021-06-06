package com.github.fanzezhen.common.gateway.core.support;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

/**
 * @author zezhen.fan
 */
public class BinderUtil {

	public static <T> void bindEureka(String prefix, Map<String, String> metadata, T prop) {
		if (metadata == null || metadata.size() == 0) {
            return;
        }
		MapConfigurationPropertySource configurationPropertyNames = new MapConfigurationPropertySource(metadata);
		Binder binder = new Binder(configurationPropertyNames);
		Bindable<T> toBind = Bindable.ofInstance(prop);
		binder.bind(prefix, toBind);
	}
}
