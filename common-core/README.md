common-core
------------------------------------------
公共核心模块，需要依赖于Redis启用缓存
    
    # Redis服务器地址
    spring.redis.host=127.0.0.1
    # Redis服务器连接端口
    spring.redis.port=6379

* exception 异常处理
    
    
    # 使用前需配置自动注入
    @SpringBootApplication(scanBasePackages = {"项目目录", "com.github.fanzezhen.common.exception.exception"})
