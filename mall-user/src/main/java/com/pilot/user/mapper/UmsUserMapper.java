package com.pilot.user.mapper;

import com.pilot.user.domain.UmsUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UmsUserMapper {
    UmsUser selectByUsername(@Param("username") String username);
    int insert(UmsUser user);
}
