package com.pilot.user.controller;

import com.pilot.common.api.CommonResult;
import com.pilot.user.dto.UserLoginParam;
import com.pilot.user.service.UmsUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UmsUserService userService;

    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody UserLoginParam param) {
        String token = userService.login(param.getUsername(), param.getPassword());
        return CommonResult.success(token);
    }
}
