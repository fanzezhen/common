MyBatis-Plus
------------------------------------------   
启用配置
   * @MapperScan(mapper文件夹路径)
   * @EnableCommonMpConfig


    # 枚举配置， 支持统配符 * 或者 ; 分割
    mybatis-plus.type-enums-package=com.github.fanzezhen.common.core.enums.db
    # 逻辑删除配置
    mybatis-plus.global-config.db-config.logic-delete-field: delFlag  # 全局逻辑删除的实体字段名(since 3.3.0)
    mybatis-plus.global-config.db-config.logic-delete-value: 1 # 逻辑已删除值(默认为 1)
    mybatis-plus.global-config.db-config.logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)

generator使用方法
   * 项目依赖该模块后使用GeneratorTool.generator()
   
    public class MppGenerator {
        public static void main(String[] args) {
            String dataSourceConfigUrl = "jdbc:mysql://localhost:3306/framework?useSSL=false&useUnicode=true&characterEncoding=UTF-8";
            String dbUsername = "framework";
            String dbPassword = "951106";
            String moduleName = "log-biz";
            String moduleParentName = "com.github.fanzezhen.framework.logbiz";
            String superEntityClassName = "com.github.fanzezhen.pojo.entity.BaseVarEntity";
            GeneratorTool.generator(new MysqlGenerator(dataSourceConfigUrl, dbUsername, dbPassword, moduleName, moduleParentName, superEntityClassName));
        }
    }

