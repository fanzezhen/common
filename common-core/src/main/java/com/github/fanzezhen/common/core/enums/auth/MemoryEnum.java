package com.github.fanzezhen.common.core.enums.auth;

public class MemoryEnum {
    /**
     * @author fanzezhen
     * 内存用户枚举类
     */
    public enum MemoryAuthEnum {
        ADMIN("admin", "admin", new String[]{RoleEnum.RoleTypeEnum.ADMIN.getCode()}, "管理员"),
        ROOT("root", "root", new String[]{RoleEnum.RoleTypeEnum.SPECIAL_ADMIN.getCode(), RoleEnum.RoleTypeEnum.ADMIN.getCode()}, "root用户"),
        GUEST("guest", "guest", new String[]{RoleEnum.RoleTypeEnum.GUEST.getCode()}, "游客");

        private final String username;
        private final String password;
        private final String[] roleTypes;
        private final String desc;

        MemoryAuthEnum(String username, String password, String[] roleTypes, String desc) {
            this.username = username;
            this.password = password;
            this.roleTypes = roleTypes;
            this.desc = desc;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String[] getRoleTypes() {
            return roleTypes;
        }

        public String getDesc() {
            return desc;
        }
    }
}
