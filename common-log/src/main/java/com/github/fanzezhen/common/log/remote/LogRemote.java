package com.github.fanzezhen.common.log.remote;

import com.github.fanzezhen.common.core.model.response.ActionResult;
import com.github.fanzezhen.common.log.foundation.entity.LogOperate;
import com.github.fanzezhen.common.log.foundation.entity.LogOperateDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

/**
 * @author zezhen.fan
 */
@FeignClient(url = "${log.url:}", name = "${log.name:base-log}")
public interface LogRemote {

    /**
     * 加载用户信息
     *
     * @param logOperate 日志封装
     * @return 日志ID
     */
    @GetMapping("/add/operate")
    ActionResult<String> addLogOperate(@RequestBody LogOperate logOperate);

    /**
     * 加载用户信息
     *
     * @param logOperateDetails 日志封装
     * @return Boolean
     */
    @GetMapping("/add/operate-detail/batch")
    ActionResult<Boolean> addLogOperateDetailBatch(@RequestBody Collection<LogOperateDetail> logOperateDetails);
}
