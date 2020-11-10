package com.github.fanzezhen.common.mp.generator;

import com.github.fanzezhen.common.core.model.entity.BaseVarAloneEntity;
import com.github.fanzezhen.common.mp.Constant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Accessors(chain = true)
@Data
public abstract class GeneratorBean {
    /**
     * 作者
     */
    private String author;
    /**
     * db链接地址
     */
    private String dataSourceConfigUrl;
    /**
     * db驱动名称
     */
    private String driverName;
    /**
     * db用户登录名称
     */
    private String dbUsername;
    /**
     * db用户登录密码
     */
    private String dbPassword;
    /**
     * 模块名数组，数据结构是为了支持多层级，单层级即为{"foundation"}或{"当前biz模块名", "foundation"}
     */
    private List<String> moduleNameList;
    /**
     * 父级包目录名
     */
    private String parentPackageName;
    /**
     * 包文件夹名
     */
    private String packageModuleName;
    /**
     * 输入表名的分隔符
     */
    private String tableNameSplitter;
    /**
     * 输入的表名字符串
     */
    private String tableNameStr;
    /**
     * 数据表模型类的父类名
     */
    private Class<?> superEntityClass;
    /**
     * 数据表模型类的父类字段（用于在模型类中省略）
     */
    private String[] superEntityColumns;
    /**
     * 数据表中的逻辑删除字段
     */
    private String logicDeleteFieldName;

    public GeneratorBean putSuperiorModuleNames(String... moduleNames) {
        if (moduleNameList == null) {
            moduleNameList = new ArrayList<>();
        }
        this.moduleNameList.addAll(Arrays.asList(moduleNames));
        return this;
    }

    public GeneratorBean setSuperiorModuleNames(String... moduleNames) {
        this.moduleNameList = new ArrayList<>(Arrays.asList(moduleNames));
        return this;
    }

    public void setSuperEntityColumns(String... superEntityColumns) {
        this.superEntityColumns = superEntityColumns;
    }

    /**
     * 初始化
     */
    public abstract void init();

    public GeneratorBean() {
        setLogicDeleteFieldName(Constant.LOGIC_DELETE_FIELD_NAME);
        setModuleNameList(new ArrayList<>() {{
            add(Constant.AUTOMATED_MODULE_NAME);
        }});
        setPackageModuleName(Constant.PACKAGE_MODULE_NAME);
        setTableNameSplitter(Constant.DEFAULT_TABLE_NAME_SPLITTER);
        setTableNameStr(GeneratorTool.scanner(Constant.DEFAULT_TABLE_NAME_INPUT_HINT));
        setSuperEntityClass(com.github.fanzezhen.common.core.model.entity.BaseVarAloneEntity.class);
        setSuperEntityColumns(BaseVarAloneEntity.getFieldNames());
        init();
    }
}
