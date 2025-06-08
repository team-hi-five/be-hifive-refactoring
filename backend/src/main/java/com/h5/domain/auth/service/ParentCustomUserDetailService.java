package com.h5.domain.auth.service;

import com.h5.domain.auth.userdetails.ParentCustomUserDetails;
import com.h5.domain.user.parent.repository.ParentUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Parent용 UserDetailsService 구현체.
 * ParentUserRepository를 사용하여 이메일 기반으로 Parent 사용자 정보를 조회하고,
 * 이를 UserDetails로 래핑하여 Spring Security 인증 컨텍스트에 제공한다.
 */
@Service
public class ParentCustomUserDetailService implements UserDetailsService {

    private final ParentUserRepository parentUserRepository;

    public ParentCustomUserDetailService(ParentUserRepository parentUserRepository) {
        this.parentUserRepository = parentUserRepository;
    }

    /**
     * 주어진 이메일을 기준으로 ParentUserEntity를 조회하고, 존재하면 ParentCustomUserDetails로 변환하여 반환한다.
     *
     * @param email 조회할 Parent 사용자의 이메일
     * @return ParentCustomUserDetails (UserDetails 구현체)
     * @throws UsernameNotFoundException 해당 이메일로 조회된 사용자가 없거나 삭제된 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return parentUserRepository.findByEmailAndDeleteDttmIsNull(email)
                .map(ParentCustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Parent user not found with email: " + email));
    }
}
