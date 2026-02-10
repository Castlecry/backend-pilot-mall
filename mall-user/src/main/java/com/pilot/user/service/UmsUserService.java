package com.pilot.user.service;

import com.pilot.common.utils.JwtUtils;
import com.pilot.user.domain.UmsUser;
import com.pilot.user.mapper.UmsUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UmsUserService {
    @Autowired
    private UmsUserMapper userMapper;

    // Spring Security 提供的加密工具，需要在父工程引入相关依赖或直接手写 MD5 替代
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String username, String password) {
        UmsUser user = userMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        return JwtUtils.createToken(user.getId(), user.getUsername());
    }
}