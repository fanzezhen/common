common-security
------------------------------------------   

启用配置

* @EnableCommonSecurityConfig：未使用cas单点登录时使用此注解
* @EnableCommonSecurityCasConfig：使用cas单点登录时使用此注解
   
   
    #应用访问地址
    security.app.url=http://localhost:${server.port}${server.servlet.context-path}
    #应用登录地址
    security.app.login.url=/login
    #应用登出地址
    security.app.logout.url=/logout
    # 不校验权限（免登陆）的接口
    security.ignoring.ant.matchers=/test, /tmp
    # 用户中心
    security.remote.sys.name=sys-public
    security.remote.sys.path=/public
    security.remote.sys.url=http://localhost:10002/public
    # 若项目使用CAS单点登录，应添加以下参数：
    # CAS服务地址
    security.cas.server.host.url:http://localhost:8080/cas
    # CAS服务登录地址
    security.cas.server.host.login_url=${security.cas.server.host.url}/login
    # CAS服务登出地址
    security.cas.server.host.logout_url=${security.cas.server.host.url}/logout?service=${security.app.url}
