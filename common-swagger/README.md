swagger
------------------------------------------   
模块介绍
   * 主体框架： Springboot 2
   
引用条件
   * 此模块需扫描该目录结构，如：
    
    @SpringBootApplication(scanBasePackages = {"项目目录", "com.github.fanzezhen.swagger"})
   
   * 此模块需要获取配置参数： 

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
