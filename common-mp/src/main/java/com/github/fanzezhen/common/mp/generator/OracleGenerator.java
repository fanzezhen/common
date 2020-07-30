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

    public OracleGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword, String moduleName,
                           String modulePackageName) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleName(moduleName);
        setModulePackageName(modulePackageName);
    }

    public OracleGenerator(String dataSourceConfigUrl, String dbUsername, String dbPassword, String moduleName,
                           String modulePackageName, String superEntityClassName, String... superEntityColumns) {
        setDataSourceConfigUrl(dataSourceConfigUrl);
        setDbUsername(dbUsername);
        setDbPassword(dbPassword);
        setModuleName(moduleName);
        setModulePackageName(modulePackageName);
        setSuperEntityClassName(superEntityClassName);
        setSuperEntityColumns(superEntityColumns);
    }

    /**
     * RUN THIS
     */
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        GeneratorTool.generator(OracleGenerator.class.getConstructor().newInstance());
    }

}
