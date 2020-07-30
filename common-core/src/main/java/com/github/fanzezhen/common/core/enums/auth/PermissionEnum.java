package com.github.fanzezhen.common.core.enums.auth;

public class PermissionEnum {
    /**
     * @description (权限类型枚举类).
     */
    public enum PermissionTypeEnum {
        MENU(1, "菜单"),
        BUTTON(2, "按钮");

        private int type;
        private String desc;

        public int getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }

        PermissionTypeEnum(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }

}
