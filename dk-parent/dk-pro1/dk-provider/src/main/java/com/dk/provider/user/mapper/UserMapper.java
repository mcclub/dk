package com.dk.provider.user.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.user.entity.User;

public interface UserMapper  extends BaseMapper<User> {
    int insert(User record);

    int insertSelective(User record);


}