package com.github.fanzezhen.common.log.interceptor;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fanzezhen.common.core.context.SysContext;
import com.github.fanzezhen.common.core.annotion.OperateLog;
import com.github.fanzezhen.common.core.annotion.OperateLogMapper;
import com.github.fanzezhen.common.core.dict.AbstractDict;
import com.github.fanzezhen.common.core.enums.table.CommonFieldEnum;
import com.github.fanzezhen.common.log.foundation.entity.LogOperate;
import com.github.fanzezhen.common.log.foundation.entity.LogOperateDetail;
import com.github.fanzezhen.common.log.foundation.service.ILogOperateDetailService;
import com.github.fanzezhen.common.log.foundation.service.ILogOperateService;
import com.github.fanzezhen.common.log.remote.LogRemote;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @author zhujiajun
 * @version 1.0
 * @since 2018/10/25 13:54
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
})
public class OperateLogInterceptor implements Interceptor {
    private static final String MAPPER_UPDATE_METHOD_PARAM_MAP_ET_KEY = "et";
    private static final String MAPPER_DELETE_METHOD_PARAM_MAP_EW_KEY = "ew";
    private static final String NAME_SUFFIX_DTO = "Dto";
    private static final String NAME_SUFFIX_SERVICE_IMPL = "ServiceImpl";
    @Value("${log.operate.check-mapper:false}")
    private boolean needCheckMapper;
    @Value("${log.operate.use-microservice:false}")
    private boolean useMicroservice;
    @Resource
    private LogRemote logRemote;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result;
        Object arg = getArg(invocation);
        OperateLog operateLog = arg == null ? null : arg.getClass().getAnnotation(OperateLog.class);
        if (operateLog == null) {
            result = invocation.proceed();
            return result;
        }
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String statementId = mappedStatement.getId();
        String type = statementId.substring(0, statementId.lastIndexOf("."));
        Class<?> mapperClass = Class.forName(type);
        if (needCheckMapper) {
            if (!mapperClass.isAnnotationPresent(OperateLogMapper.class)) {
                result = invocation.proceed();
                return result;
            }
        }
        AbstractDict dict = ReflectUtil.invokeStatic(
                ReflectUtil.getPublicMethod(operateLog.dictClass(), AbstractDict.INSTANCE_METHOD_NAME));
        boolean isAllFields = operateLog.isAllFields();
        String[] fieldNames = operateLog.fieldNameFilters();
        List<String> fieldNameList = Arrays.asList(fieldNames);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        String tableName = operateLog.tableName();
        if (StrUtil.isBlank(tableName)) {
            tableName = getTableNameByClass(arg.getClass());
        }
        LogOperate logOperate = insertLog(String.valueOf(ReflectUtil.getFieldValue(arg, "id")), sqlCommandType.ordinal(), tableName);
        switch (sqlCommandType) {
            case INSERT:
                dealInsertSqlCommandType(dict, operateLog, logOperate, arg);
                break;
            case UPDATE:
                String serviceBeanName = operateLog.serviceBeanName();
                IService<?> service = null;
                try {
                    if (StrUtil.isBlank(serviceBeanName)) {
                        serviceBeanName = getServiceBeanNameByClass(arg.getClass());
                    }
                    service = SpringUtil.getBean(serviceBeanName);
                } catch (Throwable throwable) {
                    log.warn("", throwable);
                }
                if (StrUtil.isBlank(serviceBeanName) || service == null) {
                    dealInsertSqlCommandType(dict, operateLog, logOperate, arg);
                    break;
                }
                List<LogOperateDetail> detailLogList = new ArrayList<>();
                //更新之前的PO参数
                Object oldBean = service.getById((Serializable) ReflectUtil.getFieldValue(arg, CommonFieldEnum.PK.field));
                dict.getDict().forEach((key, name) -> {
                    if (!isAllFields && !fieldNameList.contains(key)) {
                        return;
                    }
                    Object oldFieldValue = ReflectUtil.getFieldValue(oldBean, key);
                    Object newFieldValue = ReflectUtil.getFieldValue(arg, key);
                    if (null != newFieldValue && !newFieldValue.equals(oldFieldValue)) {
                        LogOperateDetail detailLog = new LogOperateDetail();
                        detailLog.setLogId(logOperate.getId());
                        String tableColumn = StrUtil.toUnderlineCase(key);
                        detailLog.setTableColumn(tableColumn);
                        detailLog.setColumnName(name);
                        //新增只有新值
                        String newValue = String.valueOf(newFieldValue);
                        if (StrUtil.isNotEmpty(newValue)) {
                            detailLog.setNewValue(newValue);
                            detailLog.setOldValue(String.valueOf(oldFieldValue));
                            detailLogList.add(detailLog);
                        }
                    }
                });
                addLogOperateDetailBatch(detailLogList);
                break;
            case DELETE:
            case SELECT:
            case FLUSH:
            case UNKNOWN:
            default:
        }
        result = invocation.proceed();
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private LogOperate insertLog(String bizId, int operateType, String tableName) {
        LogOperate logOperate = new LogOperate();
        logOperate.setBizId(bizId);
        logOperate.setTableName(tableName);
        logOperate.setOperateType(operateType);
        logOperate.setAppCode(SysContext.getCurrentAppCode());
        logOperate.setOperateUsername(SysContext.getUserName());
        if (useMicroservice) {
            logRemote.addLogOperate(logOperate);
        } else {
            ILogOperateService operateLogService = SpringUtil.getBean(ILogOperateService.class);
            operateLogService.save(logOperate);
        }
        return logOperate;
    }

    private void addLogOperateDetailBatch(Collection<LogOperateDetail> logOperateDetails) {
        ILogOperateDetailService operateLogDetailService = SpringUtil.getBean(ILogOperateDetailService.class);
        if (useMicroservice) {
            logRemote.addLogOperateDetailBatch(logOperateDetails);
        } else {
            operateLogDetailService.saveBatch(logOperateDetails);
        }
    }

    private void dealInsertSqlCommandType(AbstractDict dict, OperateLog operateLog, LogOperate logOperate, Object arg) {
        List<LogOperateDetail> detailLogList = new ArrayList<>();
        boolean isAllFields = operateLog.isAllFields();
        String[] fieldNames = operateLog.fieldNameFilters();
        List<String> fieldNameList = Arrays.asList(fieldNames);
        dict.getDict().forEach((key, name) -> {
            if (!isAllFields && !fieldNameList.contains(key)) {
                return;
            }
            Object fieldValue = ReflectUtil.getFieldValue(arg, key);
            if (null != fieldValue) {
                LogOperateDetail detailLog = new LogOperateDetail();
                detailLog.setLogId(logOperate.getId());
                String tableColumn = StrUtil.toUnderlineCase(key);
                detailLog.setTableColumn(tableColumn);
                detailLog.setColumnName(name);
                //新增只有新值
                String newValue = String.valueOf(fieldValue);
                if (StrUtil.isNotEmpty(newValue)) {
                    detailLog.setNewValue(newValue);
                    detailLogList.add(detailLog);
                }
            }
        });
        addLogOperateDetailBatch(detailLogList);
    }

    @SuppressWarnings("all")
    private Object getArg(Invocation invocation) {
        Object result = null;
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            switch (sqlCommandType) {
                case INSERT:
                    result = invocation.getArgs()[1];
                    break;
                case UPDATE:
                    result = ((Map<String, Object>) invocation.getArgs()[1]).get(MAPPER_UPDATE_METHOD_PARAM_MAP_ET_KEY);
                    break;
                case DELETE:
                    result = ((Map<String, Object>) invocation.getArgs()[1]).get(MAPPER_DELETE_METHOD_PARAM_MAP_EW_KEY);
                    break;
                default:
            }
        } catch (Throwable throwable) {
            log.warn("", throwable);
        }
        return result;
    }

    private String getTableNameByClass(Class<?> clazz) {
        return StrUtil.toUnderlineCase(StrUtil.strip(
                StrUtil.subAfter(clazz.getName(), StrUtil.C_DOT, true), StrUtil.EMPTY,
                "Dto"));
    }

    private String getServiceBeanNameByClass(Class<?> clazz) {
        return StrUtil.lowerFirst(StrUtil.strip(StrUtil.subAfter(clazz.getName(), StrUtil.C_DOT, true),
                StrUtil.EMPTY, NAME_SUFFIX_DTO)) + NAME_SUFFIX_SERVICE_IMPL;
    }

}
