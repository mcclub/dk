package com.dk.provider.user.mapper;

import com.dk.provider.user.entity.User;

import javax.annotation.Resource;
import java.util.Map;

@Resource
public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);

    User searchReferPeople(Map map);

    User isPhoneRegisterOem(Map map);
}