package com.github.fanzezhen.common.mp.base;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author fanzezhen
 * @createTime 2024/6/6 16:02
 * @since 1.0.0
 */
public class ServiceImpl<M extends BaseMapper<T>, T, B, K extends Serializable> extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T> implements IService<T, B, K> {
    protected Class<B> boClass;

    @Override
    public B toBO(T entity) {
        return BeanUtil.copyProperties(entity, boClass);
    }

    @Override
    public List<B> toBO(Collection<T> entities) {
        return BeanUtil.copyToList(entities, boClass);
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        for (Type interfaceType : this.getClass().getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == IService.class) {
                boClass = (Class<B>) parameterizedType.getActualTypeArguments()[1];
                break;
            }
        }
    }
}
