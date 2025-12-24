package com.oneself.model.bo;

import com.oneself.model.enums.StatusEnum;
import com.oneself.model.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.model.bo
 * className LoginUserBO
 * description
 * version 1.0
 */
@Data
@NoArgsConstructor
public class LoginUserBO implements UserDetails {

    @Schema(description = "用户 ID")
    private String id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户类型(0-管理员,1-普通用户)")
    private UserTypeEnum type;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "所属部门 ID")
    private String deptId;

    @Schema(description = "状态")
    private StatusEnum status;

    @Schema(description = "角色 Code 列表")
    private Set<String> roleCodes = new HashSet<>();

    @Schema(description = "权限 Code 列表")
    private Set<String> permissionCodes = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 或根据角色返回权限
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return StatusEnum.NORMAL.equals(getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return StatusEnum.NORMAL.equals(getStatus());
    }
}
