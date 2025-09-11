package com.oneself.model.bo;

import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.LoginUserVO;
import com.oneself.utils.BeanCopyUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author liuhuan
 * date 2025/9/9
 * packageName com.oneself.model.bo
 * className LoginUserBO
 * description
 * version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class LoginUserBO extends LoginUserVO implements UserDetails {

    public LoginUserBO(LoginUserVO user) {
        BeanCopyUtils.copy(user, this);
    }


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
