package com.h5.domain.auth.service;

import com.h5.domain.auth.userdetails.ConsultantCustomUserDetails;
import com.h5.domain.consultant.repository.ConsultantUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Consultant용 UserDetailsService 구현체.
 * ConsultantUserRepository를 사용하여 이메일 기반으로 Consultant 객체를 조회하고,
 * 이를 UserDetails로 래핑하여 Spring Security 인증 컨텍스트에 제공한다.
 */
@Service
public class ConsultantCustomUserDetailService implements UserDetailsService {

    private final ConsultantUserRepository consultantUserRepository;

    public ConsultantCustomUserDetailService(ConsultantUserRepository consultantUserRepository) {
        this.consultantUserRepository = consultantUserRepository;
    }

    /**
     * 주어진 이메일을 기준으로 ConsultantUserEntity를 조회하고, 존재하면 ConsultantCustomUserDetails로 변환하여 반환한다.
     *
     * @param email 조회할 Consultant 사용자의 이메일
     * @return ConsultantCustomUserDetails (UserDetails 구현체)
     * @throws UsernameNotFoundException 해당 이메일로 조회된 사용자가 없거나 삭제된 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return consultantUserRepository.findByEmailAndDeleteDttmIsNull(email)
                .map(ConsultantCustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Consultant user not found with email: " + email));
    }
}
