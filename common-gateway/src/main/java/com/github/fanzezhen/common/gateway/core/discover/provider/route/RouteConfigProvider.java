package com.github.fanzezhen.common.gateway.core.discover.provider.route;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * @author zezhen.fan
 */
public interface RouteConfigProvider {
    /**
     * 为实例加载路由定义
     *
     * @param instance 微服务实例
     * @return 微服务实例的路由定义
     */
    RouteDefinition loadRouteDefinition(ServiceInstance instance);

    /**
     * 为实例加载路由定义
     *
     * @param instance          微服务实例
     * @param checkExtranetSign 是否校验外网签名
     * @return 微服务实例的路由定义
     */
    RouteDefinition loadRouteDefinition(ServiceInstance instance, boolean checkExtranetSign);

    /**
     * 加载仅做中转的路由定义
     *
     * @return 仅做中转的路由定义
     */
    RouteDefinition loadRouteDefinition();
}
