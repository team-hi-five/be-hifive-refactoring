package com.h5.domain.child.service;

import com.h5.domain.child.repository.ChildUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildUserService {

    private final ChildUserRepository childUserRepository;
    
}
