package com.rac.ktm.userconfig;

import com.rac.ktm.entity.UserInfo;
import com.rac.ktm.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional <UserInfo> userInfo = userInfoRepository.findByUserName(username);
        return userInfo.map(CustomUserDetail::new)
                .orElseThrow(()-> new UsernameNotFoundException("user not found "+ username));
    }
}
