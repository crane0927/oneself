package com.oneself.model.bo;

import com.oneself.model.enums.StatusEnum;
import com.oneself.model.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.Data;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserBO implements UserDetails {

    private UserVO user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return StatusEnum.NORMAL.equals(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return StatusEnum.NORMAL.equals(user.getStatus());
    }
}
