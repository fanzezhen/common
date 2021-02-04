common-core
------------------------------------------

启用配置

* @EnableCommonCoreConfig


    # AppCode
    project.code=${spring.application.name}
    # 版本
    project.version=@project.version@
    # 扫描包路径
    project.base.package=@project.groupId@
    # DTO包路径，用于使用Entity直接作为参数对数据库操作时自动识别OperateLog
    project.dto.packages=
    # 项目标题
    project.title=${spring.application.name}
    # 项目描述
    project.description=description
    # 项目联系人
    project.link.man=author
    # 项目联系地址
    project.link.url=https://github.com/fanzezhen/common/tree/master/common-swagger
    # 项目联系邮箱
    project.link.email=e-mail
    # 项目许可
    project.license=The Apache License
    # 项目许可链接
    project.license.url=https://www.apache.org/licenses/LICENSE-2.0
