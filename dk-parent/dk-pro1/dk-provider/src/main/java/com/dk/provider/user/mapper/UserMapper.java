package com.dk.provider.user.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    int insert(User record);

    int insertSelective(User record);

    User searchReferPeople(Map map);

    User isPhoneRegisterOem(Map map);

    int updateByBindCard (Map map);

    User comparePassword (Map map);

    int updatePassword (Map map);

    int udpOpautopay(Map map);
}