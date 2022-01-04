common
------------------------------------------
此项目目的是为了将常用的通用功能组件化，减负实际工作中的开发任务。
此项目仅后端组件，无web页面，具体应用示例可见 https://github.com/fanzezhen/demo
1. 项目采用Maven多模块开发方式，模块介绍如下：
    * common-core                  ：核心模块
    * common-exception             ：异常处理模块
    * common-gateway               ：网关核心模块
    * common-gateway-endpoint-auth ：网关
    * common-gateway-sub-env       ：网关子环境隔离
    * common-log                   ：日志模块
    * common-mp                    ：MyBatis-Plus
    * common-redis                 ：redis缓存模块
    * common-security              ：权限模块
    * common-sentinel              ：sentinel防控模块
    * common-swagger               ：swagger接口模块
    * common-web                   ：web模块，通用的mvc配置
   
2. 项目介绍
   * 后端主体框架： Springboot 2
   * 持久层： MyBatis-Plus
   * 使用java版本： jdk 11+
   
3. 运行条件
   * 每个模块都有启用该模块配置的注解，详见各模块中的 README.md
