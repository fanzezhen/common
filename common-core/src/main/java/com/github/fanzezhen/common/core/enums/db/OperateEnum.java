package com.github.fanzezhen.common.core.enums.db;

/**
 * @author zhujiajun
 * @version 1.0
 * @since 2018/10/25 22:36
 */
public class OperateEnum {

    public enum OperateType {

        INSERT(0, "新增"),
        UPDATE(1, "修改"),
        DELETE(2, "删除"),
        TRANSFER_IN(12, "迁入"),
        TRANSFER_OUT(13, "迁出");

        private Integer code;
        private String desc;

        OperateType(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static String getDesc(Integer code) {
            for (OperateType type : OperateType.values()) {
                if (type.getCode().equals(code)) {
                    return type.getDesc();
                }
            }
            return null;
        }
    }

    public enum Source {

        PC(0, "PC");

        private Integer code;
        private String desc;

        Source(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum Module {

        SUBJECT(0, "受试者模块");

        private Integer code;
        private String desc;

        Module(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

    }

}
