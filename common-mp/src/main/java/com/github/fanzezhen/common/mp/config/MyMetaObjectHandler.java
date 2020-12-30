package com.github.fanzezhen.common.mp.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.github.fanzezhen.common.core.context.SysContext;
import com.github.fanzezhen.common.core.enums.CommonEnum;
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
        String loginUserId = SysContext.getUserId();
        log.debug("start insert fill ....");
        // 也可以使用(3.3.0 该方法有bug请升级到之后的版本如`3.3.1.8-SNAPSHOT`)
        if (StrUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, "createUserId", loginUserId);
        }
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String loginUserId = SysContext.getUserId();
        log.debug("start update fill ....");
        // 也可以使用(3.3.0 该方法有bug请升级到之后的版本如`3.3.1.8-SNAPSHOT`)
        if (StrUtil.isNotBlank(loginUserId)) {
            this.fillStrategy(metaObject, "updateUserId", loginUserId);
        }
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
        this.fillStrategy(metaObject, "status", CommonEnum.StatusEnum.VALID.getCode());
        this.fillStrategy(metaObject, "delFlag", CommonEnum.DeleteFlagEnum.NO.getCode());
    }
}
