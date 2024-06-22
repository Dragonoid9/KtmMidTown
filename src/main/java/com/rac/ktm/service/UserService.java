package com.rac.ktm.service;

import com.rac.ktm.dto.requestDto.ProfileRequestDto;
import com.rac.ktm.dto.requestDto.ProfileResponseDto;
import com.rac.ktm.dto.requestDto.UserDto;
import com.rac.ktm.entity.UserInfo;
import com.rac.ktm.repository.UserInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public void registerUser(UserDto userDto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userDto.getUsername());
        userInfo.setFullName(userDto.getFullName());
        userInfo.setEmail(userDto.getEmail());
        userInfo.setPhoneNumber(userDto.getPhoneNumber());
        userInfo.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userInfo.setRoles("USER"); // Default role
        userInfoRepository.save(userInfo);
    }

    public ProfileResponseDto profileRequest(ProfileRequestDto profileRequestDto) {
        Optional<UserInfo> userInfoOpt = userInfoRepository.findByUserName(profileRequestDto.getUserName());
        if (userInfoOpt.isPresent()) {
            UserInfo userInfo = userInfoOpt.get();
            ProfileResponseDto profileResponseDto = new ProfileResponseDto();
            profileResponseDto.setUserName(userInfo.getUserName());
            profileResponseDto.setFullName(userInfo.getFullName());
            profileResponseDto.setEmail(userInfo.getEmail());
            profileResponseDto.setPhoneNumber(userInfo.getPhoneNumber());
            profileResponseDto.setRoles(userInfo.getRoles());
            return profileResponseDto;
        }
        return null;
    }

    public void updateProfile(ProfileResponseDto profileResponseDto) {
        Optional<UserInfo> userInfoOpt = userInfoRepository.findByUserName(profileResponseDto.getUserName());
        if (userInfoOpt.isPresent()) {
            UserInfo userInfo = userInfoOpt.get();
            userInfo.setFullName(profileResponseDto.getFullName());
            userInfo.setEmail(profileResponseDto.getEmail());
            userInfo.setPhoneNumber(profileResponseDto.getPhoneNumber());
            userInfoRepository.save(userInfo);
        }
    }

    public boolean verifyPassword(String username, String currentPassword) {
        Optional<UserInfo> userInfoOpt = userInfoRepository.findByUserName(username);
        if (userInfoOpt.isPresent()) {
            UserInfo userInfo = userInfoOpt.get();
            return passwordEncoder.matches(currentPassword, userInfo.getPassword());
        }
        return false;
    }

    public UserDetails loadUserByUsername(String username) {
        Optional<UserInfo> userInfoOpt = userInfoRepository.findByUserName(username);
        if (userInfoOpt.isPresent()) {
            UserInfo userInfo = userInfoOpt.get();
            // Split roles by comma and remove the "ROLE_" prefix
            String[] roles = userInfo.getRoles().split(",");
            return User.withUsername(userInfo.getUserName())
                    .password(userInfo.getPassword())
                    .roles(roles) // Pass the roles without "ROLE_" prefix
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Extract token from "Bearer <token>"
        }
        return null;
    }

    public UserDetails getUserDetailsFromToken(String token) {
        String username = jwtService.extractUsername(token);
        // Assuming you have a method to load UserDetails by username
        return loadUserByUsername(username);
    }

    public String extractRole(String username) {
        Optional<UserInfo> userInfo=userInfoRepository.findByUserName(username);
        if (userInfo.isPresent()) {
            return userInfo.get().getRoles();
        }
        return "user";
    }
}
