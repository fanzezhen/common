package com.github.fanzezhen.common.log.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import com.github.fanzezhen.common.core.property.CommonProjectProperties;
import com.github.fanzezhen.common.core.annotion.OperateLog;
import com.github.fanzezhen.common.core.annotion.OperateLogMapper;
import com.github.fanzezhen.common.core.dict.AbstractDict;
import com.github.fanzezhen.common.mp.enums.log.OperationLogTypeEnum;
import com.github.fanzezhen.common.mp.enums.TableFieldEnum;
import com.github.fanzezhen.common.core.util.ReflectionUtil;
import com.github.fanzezhen.common.log.foundation.entity.LogOperation;
import com.github.fanzezhen.common.log.foundation.entity.LogOperationDetail;
import com.github.fanzezhen.common.log.foundation.service.ILogOperationDetailService;
import com.github.fanzezhen.common.log.foundation.service.ILogOperationService;
import com.github.fanzezhen.common.log.remote.LogRemote;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @author zezhen.fan
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
    @Resource
    private CommonProjectProperties commonProjectProperties;
    @Value("${log.operate.check-mapper:false}")
    private boolean needCheckMapper;
    @Value("${log.operate.use-microservice:false}")
    private boolean useMicroservice;
    @Resource
    private LogRemote logRemote;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = null;
        Object arg = getArg(invocation);
        OperateLog operateLog = getOperateLog(arg);
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
        if (CharSequenceUtil.isBlank(tableName)) {
            tableName = getTableNameByClass(arg.getClass());
        }
        LogOperation logOperation = insertLog(String.valueOf(ReflectUtil.getFieldValue(arg, "id")),
                sqlCommandType.ordinal(), tableName, null);
        switch (sqlCommandType) {
            case INSERT -> {
                result = invocation.proceed();
                dealOnlyNewValueLog(dict, operateLog, sqlCommandType.ordinal(), arg);
            }
            case UPDATE -> {
                IService<?> service = getService(operateLog.serviceBeanName(), arg.getClass());
                if (service == null) {
                    // 无法获取旧值，按照新增执行（operateType为更新）
                    result = invocation.proceed();
                    dealOnlyNewValueLog(dict, operateLog, sqlCommandType.ordinal(), arg);
                    break;
                }
                List<LogOperationDetail> detailLogList = new ArrayList<>();
                //更新之前的PO参数
                Object oldBean = service.getById((Serializable) ReflectUtil.getFieldValue(arg, TableFieldEnum.PK.field));
                dict.getDict().forEach((key, name) -> {
                    if (!isAllFields && !fieldNameList.contains(key)) {
                        return;
                    }
                    Object oldFieldValue = ReflectUtil.getFieldValue(oldBean, key);
                    Object newFieldValue = ReflectUtil.getFieldValue(arg, key);
                    if (null != newFieldValue && !newFieldValue.equals(oldFieldValue)) {
                        LogOperationDetail detailLog = new LogOperationDetail();
                        detailLog.setLogId(logOperation.getId());
                        String tableColumn = StrUtil.toUnderlineCase(key);
                        detailLog.setTableColumn(tableColumn);
                        detailLog.setColumnName(name);
                        //新增只有新值
                        String newValue = String.valueOf(newFieldValue);
                        if (CharSequenceUtil.isNotEmpty(newValue)) {
                            detailLog.setNewValue(newValue);
                            detailLog.setOldValue(String.valueOf(oldFieldValue));
                            detailLogList.add(detailLog);
                        }
                    }
                });
                addLogOperateDetailBatch(detailLogList);
            }
            default -> {
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private LogOperation insertLog(String bizId, int operateType, String tableName, String comment) {
        LogOperation logOperation = new LogOperation();
        logOperation.setBizId(bizId);
        logOperation.setTableName(tableName);
        logOperation.setOperationType(OperationLogTypeEnum.getOrDefaultByType(operateType));
        logOperation.setAppCode(SysContextHolder.getCurrentAppCode());
        logOperation.setOperationUsername(SysContextHolder.getUserName());
        logOperation.setRemark(comment);
        if (useMicroservice) {
            logRemote.addLogOperate(logOperation);
        } else {
            ILogOperationService operateLogService = SpringUtil.getBean(ILogOperationService.class);
            operateLogService.save(logOperation);
        }
        return logOperation;
    }

    private void addLogOperateDetailBatch(Collection<LogOperationDetail> logOperationDetails) {
        ILogOperationDetailService operateLogDetailService = SpringUtil.getBean(ILogOperationDetailService.class);
        if (useMicroservice) {
            logRemote.addLogOperateDetailBatch(logOperationDetails);
        } else {
            operateLogDetailService.saveBatch(logOperationDetails);
        }
    }

    /**
     * 处理只有新值的日志
     *
     * @param dict        字典
     * @param operateLog  日志注解
     * @param operateType 操作类型
     * @param arg         数据库入参
     */
    private void dealOnlyNewValueLog(AbstractDict dict,
                                     OperateLog operateLog,
                                     int operateType,
                                     Object arg) {
        String tableName = operateLog.tableName();
        if (CharSequenceUtil.isBlank(tableName)) {
            tableName = getTableNameByClass(arg.getClass());
        }
        String comment = null;
        if (operateType != SqlCommandType.INSERT.ordinal()) {
            comment = "未能成功加载旧值";
        }
        LogOperation logOperation = insertLog(String.valueOf(ReflectUtil.getFieldValue(arg, "id")),
                operateType, tableName, comment);
        List<LogOperationDetail> detailLogList = new ArrayList<>();
        boolean isAllFields = operateLog.isAllFields();
        String[] fieldNames = operateLog.fieldNameFilters();
        List<String> fieldNameList = Arrays.asList(fieldNames);
        dict.getDict().forEach((key, name) -> {
            if (!isAllFields && !fieldNameList.contains(key)) {
                return;
            }
            Object fieldValue = ReflectUtil.getFieldValue(arg, key);
            if (null != fieldValue) {
                LogOperationDetail detailLog = new LogOperationDetail();
                detailLog.setLogId(logOperation.getId());
                String tableColumn = StrUtil.toUnderlineCase(key);
                detailLog.setTableColumn(tableColumn);
                detailLog.setColumnName(name);
                //新增只有新值
                String newValue = String.valueOf(fieldValue);
                if (CharSequenceUtil.isNotEmpty(newValue)) {
                    detailLog.setNewValue(newValue);
                    detailLogList.add(detailLog);
                }
            }
        });
        addLogOperateDetailBatch(detailLogList);
    }

    @SuppressWarnings("all")
    private Object getArg(Invocation invocation) {
        Object arg = null;
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            switch (sqlCommandType) {
                case INSERT:
                    arg = invocation.getArgs()[1];
                    break;
                case UPDATE:
                    arg = ((Map<String, Object>) invocation.getArgs()[1]).get(MAPPER_UPDATE_METHOD_PARAM_MAP_ET_KEY);
                    break;
                case DELETE:
                    arg = ((Map<String, Object>) invocation.getArgs()[1]).get(MAPPER_DELETE_METHOD_PARAM_MAP_EW_KEY);
                    break;
                default:
            }
        } catch (Throwable throwable) {
            log.warn("", throwable);
        }
        return arg;
    }

    private String getTableNameByClass(Class<?> clazz) {
        return StrUtil.toUnderlineCase(StrUtil.strip(
                StrUtil.subAfter(clazz.getName(), StrUtil.C_DOT, true), StrUtil.EMPTY, NAME_SUFFIX_DTO));
    }

    private String getServiceBeanNameByBeanClass(Class<?> clazz) {
        return StrUtil.lowerFirst(StrUtil.strip(StrUtil.subAfter(clazz.getName(), StrUtil.C_DOT, true),
                StrUtil.EMPTY, NAME_SUFFIX_DTO)) + NAME_SUFFIX_SERVICE_IMPL;
    }

    private OperateLog getOperateLog(Object arg) {
        OperateLog operateLog = arg == null ? null : arg.getClass().getAnnotation(OperateLog.class);
        if (operateLog == null) {
            if (arg != null && CharSequenceUtil.isNotBlank(commonProjectProperties.getDtoPackage())) {
                String[] dtoPackages = commonProjectProperties.getDtoPackage().split(",");
                for (String dtoPackage : dtoPackages) {
                    Reflections reflections = new Reflections(dtoPackage);
                    for (Class<?> dtoClass : reflections.getTypesAnnotatedWith(OperateLog.class)) {
                        if (ReflectionUtil.isSubClass(dtoClass, arg.getClass())) {
                            arg = BeanUtil.copyProperties(arg, dtoClass);
                            return arg.getClass().getAnnotation(OperateLog.class);
                        }
                    }
                }
            }
        }
        return operateLog;
    }

    private IService<?> getService(String serviceBeanName, Class<?> clazz) {
        try {
            if (CharSequenceUtil.isBlank(serviceBeanName)) {
                serviceBeanName = getServiceBeanNameByBeanClass(clazz);
            }
            return CharSequenceUtil.isBlank(serviceBeanName) ? null : SpringUtil.getBean(serviceBeanName);
        } catch (Throwable throwable) {
            log.warn("", throwable);
        }
        return null;
    }
}
