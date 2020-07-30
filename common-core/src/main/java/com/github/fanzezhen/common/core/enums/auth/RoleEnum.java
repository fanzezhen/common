package com.github.fanzezhen.common.core.enums.auth;


import com.github.fanzezhen.common.core.constant.SecurityConstant;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class RoleEnum {
    /**
     * @author fanzezhen
     * 角色类型枚举类
     */
    public enum RoleTypeEnum {
        SPECIAL_ADMIN(0, "SPECIAL_ADMIN", "超级管理员"),
        ADMIN(1, "ADMIN", "管理员"),
        NORMAL(2, "NORMAL", "普通角色"),
        GUEST(3, "GUEST", "访客角色");

        private int type;
        private String code;
        private String desc;

        RoleTypeEnum(int type, String code, String desc) {
            this.type = type;
            this.code = code;
            this.desc = desc;
        }

        public int getType() {
            return type;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static List<RoleTypeEnum> queryRoleTypeEnumListByType(Collection<? extends Integer> typeList) {
            List<RoleTypeEnum> roleTypeEnumList = new ArrayList<>();
            for (RoleTypeEnum roleTypeEnum : values()) {
                if (typeList.contains(roleTypeEnum.type)) {
                    roleTypeEnumList.add(roleTypeEnum);
                }
            }
            return roleTypeEnumList;
        }

        public static Set<String> queryRoleTypeCodeSetByType(Collection<? extends Integer> typeList) {
            Set<String> roleTypeCodeSet = new HashSet<>();
            for (RoleTypeEnum roleTypeEnum : values()) {
                if (typeList.contains(roleTypeEnum.type)) {
                    roleTypeCodeSet.add(roleTypeEnum.code);
                }
            }
            return roleTypeCodeSet;
        }

        public static Set<String> securityRoleTypeCodeSetByType(Collection<? extends Integer> types) {
            Set<String> roleTypeCodeSet = new HashSet<>();
            if (CollectionUtils.sizeIsEmpty(types)) return roleTypeCodeSet;
            for (RoleTypeEnum roleTypeEnum : values()) {
                if (types.contains(roleTypeEnum.type)) {
                    roleTypeCodeSet.add(SecurityConstant.ROLE_PREFIX + roleTypeEnum.code);
                }
            }
            return roleTypeCodeSet;
        }

        public static Set<String> queryRoleTypeCodeSetAll() {
            Set<String> roleTypeCodeSet = new HashSet<>();
            for (RoleTypeEnum roleTypeEnum : values()) {
                roleTypeCodeSet.add(roleTypeEnum.code);
            }
            return roleTypeCodeSet;
        }
    }

    public static void main(String[] args) {
        System.out.println(RoleTypeEnum.ADMIN);
    }
}
