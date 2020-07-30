package com.github.fanzezhen.common.mp.generator;

import lombok.Data;

@Data
public abstract class GeneratorBean {
    private String author;
    private String dataSourceConfigUrl;
    private String driverName;
    private String dbUsername;
    private String dbPassword;
    private String moduleName;
    private String packageName;
    private String modulePackageName;
    private String tableNameSplitter;
    private String tables;
    private String superEntityClassName;
    private String[] superEntityColumns;

    public void setSuperEntityColumns(String... superEntityColumns) {
        this.superEntityColumns = superEntityColumns;
    }

    public abstract void init();

    public GeneratorBean() {
        setAuthor("fanzezhen");
        setPackageName("foundation");
        setModuleName("mpp-generator");
        setModulePackageName("com.github.fanzezhen.generator");
        setTableNameSplitter(",");
        setTables(GeneratorTool.scanner("表名"));
        setSuperEntityClassName("com.github.fanzezhen.pojo.entity.BaseVarEntity");
        setSuperEntityColumns("id", "create_time", "create_user_id");
        init();
    }
}
