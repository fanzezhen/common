package com.github.fanzezhen.common.core.generator;

import com.github.fanzezhen.common.core.model.entity.BaseEntity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * mysql 代码生成器演示例子
 * </p>
 *
 * @author fanzezhen
 * @since 2018-09-12
 */
@Accessors(chain = true)
@Builder
@NoArgsConstructor
public class MysqlGenerator extends AbstractGeneratorBean {
    @Override
    public void init() {
        setDataSourceConfigUrl("jdbc:mysql://localhost:3306/framework?useSSL=false&useUnicode=true&characterEncoding=UTF-8");
        setDriverName("com.mysql.cj.jdbc.Driver");
        setDbUsername("root");
        setDbPassword("root");
    }

    /**
     * RUN THIS
     */
    public static void main(String[] args) {
        String dataSourceConfigUrl = "jdbc:mysql://localhost:3306/demo?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String dbUsername = "root";
        String dbPassword = "root";
        String moduleParentName = "com.github.fanzezhen.common.mp";
        GeneratorTool.generator(MysqlGenerator.builder().build()
                .setAuthor("fanzezhen")
                .setDataSourceConfigUrl(dataSourceConfigUrl)
                .setDbUsername(dbUsername)
                .setDbPassword(dbPassword)
                .setSuperiorModuleNames("common-mp")
                .setParentPackageName(moduleParentName)
                .setSuperEntityClass(BaseEntity.class));
    }
}
