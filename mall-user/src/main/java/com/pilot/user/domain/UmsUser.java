package com.pilot.user.domain;

import lombok.Data;

import java.util.Date;

@Data
public class UmsUser {
    private Long id;
    private String username;
    private String password;
    private Date createTime;
}
