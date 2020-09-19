package com.github.fanzezhen.common.mp.generator;

import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 * mysql 代码生成器演示例子
 * </p>
 *
 * @author fanzezhen
 * @since 2018-09-12
 */
@NoArgsConstructor
public class MysqlGenerator extends GeneratorBean {
    @Override
    public void init() {
        setDataSourceConfigUrl("jdbc:mysql://localhost:3306/framework?useSSL=false&useUnicode=true&characterEncoding=UTF-8");
        setDriverName("com.mysql.cj.jdbc.Driver");
        setDbUsername("root");
        setDbPassword("root");
    }

    public MysqlGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword,
                          String modulePackageName, String... moduleNames) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleNames(moduleNames);
        setModulePackageName(modulePackageName);
    }

    public MysqlGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword,
                          String modulePackageName, String superEntityClassName, String... moduleNames) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleNames(moduleNames);
        setModulePackageName(modulePackageName);
        setSuperEntityClassName(superEntityClassName);
    }

    public MysqlGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword, String[] moduleNames,
                          String modulePackageName, String superEntityClassName, String... superEntityColumns) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleNames(moduleNames);
        setModulePackageName(modulePackageName);
        setSuperEntityClassName(superEntityClassName);
        setSuperEntityColumns(superEntityColumns);
    }

    public MysqlGenerator(String author, String dataSourceConfigUrl, String driverName, String dbUsername, String dbPassword,
                          String packageName, String modulePackageName, String tableNameSplitter, String tables) {
        setAuthor(author);
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDriverName(driverName);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setPackageName(packageName);
        setModulePackageName(modulePackageName);
        setTableNameSplitter(tableNameSplitter);
        setTables(tables);
    }

    /**
     * RUN THIS
     */
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        GeneratorTool.generator(MysqlGenerator.class.getConstructor().newInstance());
    }

}
