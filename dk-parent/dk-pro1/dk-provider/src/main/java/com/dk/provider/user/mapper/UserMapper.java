package com.dk.provider.user.mapper;

import com.dk.provider.user.entity.User;

public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);
}