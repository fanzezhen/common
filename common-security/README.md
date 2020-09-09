security
------------------------------------------   
模块介绍
   * 主体框架： Springboot 2
   * 持久层： MyBatis-Plus
   
引用条件
   * 此模块需扫描该目录结构，如：
    
    @SpringBootApplication(scanBasePackages = {"项目目录", "com.github.fanzezhen.security.interceptor", "com.github.fanzezhen.security.property"})
   
   * 此模块需要获取配置参数： 
   
    #应用访问地址
    security.app.url=http://localhost:${server.port}${server.servlet.context-path}
    #应用登录地址
    security.app.login.url=/login
    #应用登出地址
    security.app.logout.url=/logout
    # 不校验权限的接口
    security.ignoring.ant.matchers=/test, /tmp
    # 是否返回json
    security.response.json=false
    # 项目属性参数
    project.code=SYS
    project.version=@projectVersion@
    project.base.package=@projectBasePackage@
    project.title=@projectTitle@
    project.description=@projectDescription@
    project.link.man=@projectLinkMan@
    project.link.url=@projectLinkUrl@
    project.link.email=@projectLinkEmail@
    project.license=@projectLicense@
    project.license.url=@projectLicenseUrl@

   * 应添加权限查询服务ID或地址参数，并在启动时扫描该目录：
        
    # 用户中心接口地址
    security.remote.user.detail.url=http://localhost:10002/framework/public
    @EnableFeignClients(basePackages = {"项目remote目录", "com.github.fanzezhen.security.facade.feign"})
    @SpringBootApplication(scanBasePackages = {"com.github.fanzezhen.security.facade.feign")

   * 若项目使用CAS单点登录，应在启动时扫描该目录：
        
    # CAS服务地址
    security.cas.server.host.url=http://testcas.sgst.cn/cas
    # CAS服务登录地址
    security.cas.server.host.login_url=${security.cas.server.host.url}/login
    # CAS服务登出地址
    security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.url}
    
    @SpringBootApplication(scanBasePackages = {"com.github.fanzezhen.security.cas")
