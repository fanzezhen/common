package com.github.fanzezhen.common.log.foundation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fanzezhen.common.log.foundation.entity.LogOperation;
import com.github.fanzezhen.common.log.foundation.mapper.LogOperationMapper;
import com.github.fanzezhen.common.log.foundation.service.ILogOperationService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author fanzezhen
 * @since 2021-04-28
 */
@Service
public class LogOperationServiceImpl extends ServiceImpl<LogOperationMapper, LogOperation> implements ILogOperationService {

}
