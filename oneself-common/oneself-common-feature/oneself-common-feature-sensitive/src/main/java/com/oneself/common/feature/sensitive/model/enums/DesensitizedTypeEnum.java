package com.oneself.common.feature.sensitive.model.enums;

import lombok.Getter;

import java.util.function.Function;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.model.enums
 * enumName DesensitizedTypeEnum
 * description 脱敏类型枚举
 * version 1.0
 */
@Getter
public enum DesensitizedTypeEnum {

    /**
     * 中文姓名：第2位星号替换（如：李*、张*三）
     */
    CHINESE_NAME(0, "中文姓名",
            s -> s != null ? s.replaceAll("(\\S)\\S(\\S*)", "$1*$2") : null),

    /**
     * 身份证号：中间10位星号替换（如：1101**********1234、3101**********567X）
     */
    ID_CARD(1, "身份证号",
            s -> s != null ? s.replaceAll("(\\d{4})\\d{10}(\\d{3}[Xx]|\\d{4})", "$1**********$2") : null),

    /**
     * 固定电话：区号后加星号，保留最后4位（如：010-****1234、021-****5678）
     */
    FIXED_PHONE(2, "固定电话",
            s -> s != null ? s.replaceAll("(\\d{3,4}-|\\d{3,4})\\d{4}(\\d{4})", "$1****$2") : null),

    /**
     * 手机号：中间4位星号替换（如：138****1234、186****5678）
     */
    MOBILE_PHONE(3, "手机号",
            s -> s != null ? s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : null),

    /**
     * 地址：保留前6位（省市区前半段），后续星号替换（如：北京市海淀区********）
     */
    ADDRESS(4, "地址",
            s -> s != null && s.length() > 6 ? s.substring(0, 6) + "********" : s),

    /**
     * 电子邮箱：仅显示第一个字母和@后面的地址，中间星号替换（如：a****@163.com、z****@qq.com）
     */
    EMAIL(5, "电子邮箱",
            s -> s != null ? s.replaceAll("(^.)[^@]*(@.*$)", "$1****$2") : null),

    /**
     * 银行卡号：保留前6位和后4位，中间星号替换（如：622600*******1234）
     */
    BANK_CARD(6, "银行卡号",
            s -> s != null && s.length() > 10 ? s.substring(0, 6) + "*******" + s.substring(s.length() - 4) : s),

    /**
     * 密码：全部字符用*代替（如：********）
     */
    PASSWORD(7, "密码",
            s -> s != null ? "*".repeat(s.length()) : null),

    /**
     * 车牌号：普通车牌隐藏中间1位（如：京A*1234），新能源车牌隐藏中间2位（如：京A**1234）
     */
    CAR_LICENSE(8, "车牌号",
            s -> {
                if (s == null) return null;
                // 普通车牌（7位）：京A12345 → 京A*2345；新能源车牌（8位）：京A123456 → 京A**3456
                return s.length() == 7 ? s.replaceAll("(^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1})[A-Z0-9]{1}([A-Z0-9]{4})$", "$1*$2")
                        : s.length() == 8 ? s.replaceAll("(^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1})[A-Z0-9]{2}([A-Z0-9]{4})$", "$1**$2")
                        : s;
            }),

    /**
     * 密钥：保留前3位和后3位，中间星号替换（如：abc****xyz）
     */
    KEY(9, "密钥",
            s -> s != null && s.length() > 6 ? s.substring(0, 3) + "****" + s.substring(s.length() - 3) : s),

    /**
     * 自定义规则：默认返回原字符串（需外部指定自定义脱敏逻辑）
     */
    CUSTOM(10, "自定义规则",
            s -> s),

    /**
     * 无需脱敏：直接返回原始字符串（用于明确标识不需要脱敏的字段）
     */
    NONE(11, "无需脱敏", s -> s);

    /**
     * 枚举编码（用于标识）
     */
    private final Integer code;

    /**
     * 枚举描述（用于说明）
     */
    private final String desc;

    /**
     * 脱敏处理器（用于执行具体脱敏逻辑）
     */
    private final Function<String, String> desensitize;

    /**
     * 构造器：初始化编码、描述和脱敏处理器
     *
     * @param code        编码
     * @param desc        描述
     * @param desensitize 脱敏处理器
     */
    DesensitizedTypeEnum(Integer code, String desc, Function<String, String> desensitize) {
        this.code = code;
        this.desc = desc;
        this.desensitize = desensitize;
    }

    /**
     * 执行脱敏操作
     *
     * @param original 原始字符串
     * @return 脱敏后字符串
     */
    public String desensitize(String original) {
        return this.desensitize.apply(original);
    }

    /**
     * 根据编码获取枚举实例（用于快速匹配）
     *
     * @param code 枚举编码
     * @return 对应的枚举实例，无匹配时返回null
     */
    public static DesensitizedTypeEnum getByCode(Integer code) {
        for (DesensitizedTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
