package com.github.fanzezhen.common.mp.generator;

import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 * Oracle 代码生成器演示例子
 * </p>
 *
 * @author fanzezhen
 * @since 2018-09-12
 */
@NoArgsConstructor
public class OracleGenerator extends GeneratorBean {
    @Override
    public void init() {
        setDataSourceConfigUrl("jdbc:oracle:thin:@//10.10.28.42:1521/genome");
        setDriverName("oracle.jdbc.driver.OracleDriver");
        setDbUsername("tzbrck");
        setDbPassword("a123456");
    }

    public OracleGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword,
                          String modulePackageName, String... moduleNames) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleNames(moduleNames);
        setModulePackageName(modulePackageName);
    }

    public OracleGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword,
                          String modulePackageName, String superEntityClassName, String... moduleNames) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleNames(moduleNames);
        setModulePackageName(modulePackageName);
        setSuperEntityClassName(superEntityClassName);
    }

    public OracleGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword, String[] moduleNames,
                          String modulePackageName, String superEntityClassName, String... superEntityColumns) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleNames(moduleNames);
        setModulePackageName(modulePackageName);
        setSuperEntityClassName(superEntityClassName);
        setSuperEntityColumns(superEntityColumns);
    }

    public OracleGenerator(String author, String dataSourceConfigUrl, String driverName, String dbUsername, String dbPassword,
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
        GeneratorTool.generator(OracleGenerator.class.getConstructor().newInstance());
    }

}
