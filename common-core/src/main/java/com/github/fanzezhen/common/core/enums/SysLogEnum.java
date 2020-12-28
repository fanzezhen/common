package com.github.fanzezhen.common.core.enums;

/**
 * @author zezhen.fan
 */
public class SysLogEnum {
    /**
     * 登录日志类型枚举
     */
    public enum LoginLogTypeEnum {
        /**
         * 登录成功
         */
        LOGIN_SUCCEED(1, "登录成功"),
        /**
         * 登录失败
         */
        LOGIN_FAILED(2, "登录失败"),
        /**
         * 退出登录
         */
        LOGOUT(3, "退出登录");

        private final int type;
        private final String desc;

        public int getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }

        LoginLogTypeEnum(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }

    /**
     * 登录日志类型枚举
     */
    public enum OperationLogTypeEnum {
        /**
         * 新增
         */
        ADD(1, "新增"),
        /**
         * 修改
         */
        UPDATE(2, "修改");

        private final int type;
        private final String desc;

        public int getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }

        OperationLogTypeEnum(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public static boolean needCompare(int type){
            return type != ADD.type;
        }
    }
}
