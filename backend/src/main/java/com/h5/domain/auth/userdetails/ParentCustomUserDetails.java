package com.h5.domain.auth.userdetails;

import com.h5.domain.user.parent.entity.ParentUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ParentCustomUserDetails implements UserDetails {

    private final String email;
    private final String pwd;
    private final boolean isTempPwd;

    public ParentCustomUserDetails(ParentUserEntity parentUserEntity) {
        this.email = parentUserEntity.getEmail();
        this.pwd = parentUserEntity.getPwd();
        this.isTempPwd = parentUserEntity.isTempPwd();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_PARENT"));
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
