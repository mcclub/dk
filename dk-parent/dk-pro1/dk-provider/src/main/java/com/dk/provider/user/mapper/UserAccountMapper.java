package com.dk.provider.user.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.user.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Resource;
import java.util.Map;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    int deleteByPrimaryKey(Long id);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKey(UserAccount record);

    int updatePayPassword (Map map);

    UserAccount payVerification (Map map);
}