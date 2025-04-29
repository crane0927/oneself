package com.oneself.user.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.oneself.common.model.enums.StatusEnum;
import com.oneself.model.pojo.BasePojo;
import com.oneself.user.model.enums.SexEnum;
import com.oneself.user.model.enums.TypeEnum;
import lombok.*;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author liuhuan
 * date 2025/1/17
 * packageName com.oneself.user.model.pojo
 * className User
 * description 用户信息表实体类
 * version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BasePojo {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long deptId;
    private String loginName;
    private String username;
    private String password;
    private SexEnum sex;
    private TypeEnum userType;
    private StatusEnum status;
    private String avatar;
    private String email;
    private String phone;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    private String remark;


}
