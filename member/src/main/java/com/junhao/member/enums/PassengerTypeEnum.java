package com.junhao.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;


@Getter
@AllArgsConstructor
public enum PassengerTypeEnum {
    ADULT("1", "成人"),
    CHILD("2", "儿童"),
    STUDENT("3", "学生");

    private final String code;

    private final String desc;

    public static List<Map<String, String>> getEnumList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (PassengerTypeEnum anEnum : EnumSet.allOf(PassengerTypeEnum.class)) {
            Map<String, String> map = new HashMap<>();
            map.put("code", anEnum.code);
            map.put("desc", anEnum.desc);
            list.add(map);
        }
        return list;
    }
}
