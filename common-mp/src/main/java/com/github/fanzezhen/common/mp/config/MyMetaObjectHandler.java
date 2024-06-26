package com.github.fanzezhen.common.mp.config;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import com.github.fanzezhen.common.mp.base.entity.BaseGenericEntity;
import com.github.fanzezhen.common.mp.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author zezhen.fan
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        String loginUserId = SysContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, "createUserId", loginUserId);
        }
        this.fillStrategy(metaObject, "status", StatusEnum.ENABLE);
        this.fillStrategy(metaObject, "delFlag", BaseGenericEntity.DEFAULT_DEL_FLAG);
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        String loginUserId = SysContextHolder.getUserId();
        if (CharSequenceUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, "updateUserId", loginUserId);
        }
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }
}
