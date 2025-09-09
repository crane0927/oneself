package com.oneself.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oneself.annotation.Sensitive;
import com.oneself.model.enums.DesensitizedTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author liuhuan
 * date 2024/12/30
 * packageName com.oneself.model.dto
 * className SensitiveDTO
 * description  脱敏测试数据传输对象
 * version 1.0
 */
@Data
@Schema(description = "脱敏测试数据传输对象")
public class SensitiveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 1. 中文姓名脱敏（如：张三 → 张*，李四 → 李*）
    @Sensitive(DesensitizedTypeEnum.CHINESE_NAME)
    @Schema(description = "中文姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "姓名不能为空")
    private String realName;

    // 2. 手机号脱敏（如：13812345678 → 138****5678）
    @Sensitive(DesensitizedTypeEnum.MOBILE_PHONE)
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobilePhone;

    // 3. 身份证号脱敏（18位：前6后4，中间10位星号；如：110101199001011234 → 110101********1234）
    @Sensitive(DesensitizedTypeEnum.ID_CARD)
    @Schema(description = "身份证号（18位）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCard;

    // 4. 邮箱脱敏（@前保留1-2位，其余星号；如：zhangsan123@qq.com → zh****123@qq.com 或 z*@qq.com）
    @Sensitive(DesensitizedTypeEnum.EMAIL)
    @Schema(description = "邮箱地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;

    // 5. 地址脱敏（保留省+市，其余星号；如：北京市海淀区中关村大街1号 → 北京市海淀区********）
    @Sensitive(DesensitizedTypeEnum.ADDRESS)
    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "地址不能为空")
    private String address;

    // 6. 银行卡号脱敏（保留前6后4，中间星号；如：6222021234567890123 → 622202*********0123）
    @Sensitive(DesensitizedTypeEnum.BANK_CARD)
    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "银行卡号不能为空")
    @Pattern(regexp = "^\\d{16,19}$", message = "银行卡号格式不正确（16-19位数字）")
    private String bankCard;

    // 7. 固定电话脱敏（区号+星号+后缀；如：010-88888888 → 010-****8888，021-1234567 → 021-****567）
    @Sensitive(DesensitizedTypeEnum.FIXED_PHONE)
    @Schema(description = "固定电话（带区号，如010-88888888）")
    private String fixedPhone;

    // 8. 密码脱敏（全部星号，无论长度；如：123456 → ******，Abc@123 → *******）
    @Sensitive(DesensitizedTypeEnum.PASSWORD)
    @Schema(description = "密码（脱敏后全为星号）")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$", message = "密码需8-16位，含大小写、数字和特殊字符")
    private String password;

    // 9. 无需脱敏（默认NONE，用于对比）
    @Sensitive(DesensitizedTypeEnum.NONE)
    @Schema(description = "无需脱敏的字段（如用户名）")
    @NotBlank(message = "用户名不能为空")
    private String username;

    // 10. 忽略序列化（不参与接口传输，仅作示例）
    @JsonIgnore
    @Schema(description = "忽略序列化的字段（不返回给前端）", hidden = true)
    private String ignoreField;
}
