package com.oneself.script.model.bo;

import com.oneself.annotation.Sensitive;
import com.oneself.script.model.enums.ExecuteModeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "凭证 BO")
public class CredentialBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主机 IP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "IP 不能为空")
    // 可根据需求调整 IPv4/IPv6 正则
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$",
            message = "IP 格式不正确")
    private String ip;

    @Schema(description = "执行用户", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "执行用户不能为空")
    @Size(max = 50, message = "执行用户长度不能超过 50 个字符")
    private String user;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过 100 个字符")
    @Sensitive // 避免日志打印/序列化明文
    private String password;

    @Schema(description = "超时时间（秒）", example = "30")
    @NotNull(message = "超时时间不能为空")
    @Min(value = 1, message = "超时时间必须大于 0 秒")
    private Integer timeout;

    @Schema(description = "执行方式（LOCAL-本地，REMOTE-远程）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "执行方式不能为空")
    private ExecuteModeEnum executeMode;

    @Schema(description = "远程 IP 列表（当执行方式为 REMOTE 时必填）")
    private List<@Pattern(
            regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$",
            message = "远程 IP 格式不正确") String> remoteIps;
}
