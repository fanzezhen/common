package com.github.fanzezhen.common.core.generator;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Oracle 代码生成器演示例子
 * </p>
 *
 * @author fanzezhen
 * @since 2018-09-12
 */
@Builder
@NoArgsConstructor
public class OracleGenerator extends AbstractGeneratorBean {
    @Override
    public void init() {
        setDataSourceConfigUrl("jdbc:oracle:thin:@//10.10.28.42:1521/genome");
        setDriverName("oracle.jdbc.driver.OracleDriver");
        setDbUsername("tzbrck");
        setDbPassword("a123456");
    }

    /**
     * RUN THIS
     */
    public static void main(String[] args) {
        GeneratorTool.generator(OracleGenerator.builder().build());
    }

}
