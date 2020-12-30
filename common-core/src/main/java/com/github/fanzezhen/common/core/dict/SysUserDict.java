package com.github.fanzezhen.common.core.dict;


import com.github.fanzezhen.common.core.enums.CommonEnum;

import java.util.LinkedHashMap;

/**
 * @author zezhen.fan
 */
public class SysUserDict extends AbstractDict {

    @Override
    public void init() {
        put("username", "用户名");
        put("email", "邮箱");
        put("phone", "手机号");
        put("sex", "性别");
    }

    @Override
    protected void initReverse() {
        putReverse("用户名", "username");
        putReverse("手机号", "phone");
        putReverse("邮箱", "email");
        putReverse("性别", "sex");
    }

    @Override
    protected void initBeWrapped() {
        putFieldWrapper("sex", CommonEnum.SexEnum.WOMAN.getCode(), CommonEnum.SexEnum.WOMAN.getDesc());
        putFieldWrapper("sex", CommonEnum.SexEnum.MAN.getCode(), CommonEnum.SexEnum.MAN.getDesc());
        putFieldWrapper("sex", CommonEnum.SexEnum.UNKNOWN.getCode(), CommonEnum.SexEnum.UNKNOWN.getDesc());
        putFieldWrapper("sex", CommonEnum.SexEnum.UNSPECIFIED.getCode(), CommonEnum.SexEnum.UNSPECIFIED.getDesc());
    }

    @Override
    protected void initBeAdapter() {
        putFieldAdapter("sex", new LinkedHashMap<>(4) {{
            put(CommonEnum.SexEnum.WOMAN.getDesc(), CommonEnum.SexEnum.WOMAN.getCode());
            put(CommonEnum.SexEnum.MAN.getDesc(), CommonEnum.SexEnum.MAN.getCode());
            put(CommonEnum.SexEnum.UNKNOWN.getDesc(), CommonEnum.SexEnum.UNKNOWN.getCode());
            put(CommonEnum.SexEnum.UNSPECIFIED.getDesc(), CommonEnum.SexEnum.UNSPECIFIED.getCode());
        }});
    }

    private static class SingletonHolder {
        public static SysUserDict INSTANCE = new SysUserDict();
    }

    public static SysUserDict getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
