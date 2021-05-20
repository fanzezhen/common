package com.github.fanzezhen.common.log.foundation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.fanzezhen.common.log.foundation.entity.LogLogin;
import com.github.fanzezhen.common.log.foundation.mapper.LogLoginMapper;
import com.github.fanzezhen.common.log.foundation.service.ILogLoginService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 登录日志表 服务实现类
 * </p>
 *
 * @author fanzezhen
 * @since 2021-04-28
 */
@Service
public class LogLoginServiceImpl extends ServiceImpl<LogLoginMapper, LogLogin> implements ILogLoginService {

}
