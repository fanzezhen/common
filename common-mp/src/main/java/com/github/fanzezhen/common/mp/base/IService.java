package com.github.fanzezhen.common.mp.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author fanzezhen
 * @createTime 2024/6/6 16:01
 * @since 1.0.0
 */
public interface IService<T, B, K extends Serializable> extends com.baomidou.mybatisplus.extension.service.IService<T> {
    default B get(K pk) {
        return toBO(getById(pk));
    }

    B toBO(T entity);

    List<B> toBO(Collection<T> entities);

}
