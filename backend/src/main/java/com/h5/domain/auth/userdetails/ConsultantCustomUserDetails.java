package com.h5.domain.auth.userdetails;

import com.h5.domain.user.consultant.entity.ConsultantUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ConsultantCustomUserDetails implements UserDetails {

    private final String email;
    private final String pwd;
    private final boolean isTempPwd;

    public ConsultantCustomUserDetails(ConsultantUserEntity consultantUserEntity) {
        this.email = consultantUserEntity.getEmail();
        this.pwd = consultantUserEntity.getPwd();
        this.isTempPwd = consultantUserEntity.isTempPwd();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CONSULTANT"));
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
