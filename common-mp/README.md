MyBatis-Plus
------------------------------------------   
模块介绍
   * 主体框架： Springboot 2
   * 持久层： MyBatis-Plus

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

逻辑删除配置
        
    mybatis-plus:
      global-config:
        db-config:
          logic-delete-field: delFlag  # 全局逻辑删除的实体字段名(since 3.3.0)
          logic-delete-value: 1 # 逻辑已删除值(默认为 1)
          logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
