package com.github.fanzezhen.common.log.foundation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fanzezhen.common.log.foundation.entity.ExceptionLog;
import com.github.fanzezhen.common.log.foundation.mapper.LogExceptionMapper;
import com.github.fanzezhen.common.log.foundation.service.ILogExceptionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 异常日志表 服务实现类
 * </p>
 *
 * @author fanzezhen
 * @since 2021-04-28
 */
@Service
public class LogExceptionServiceImpl extends ServiceImpl<LogExceptionMapper, ExceptionLog> implements ILogExceptionService {

}
