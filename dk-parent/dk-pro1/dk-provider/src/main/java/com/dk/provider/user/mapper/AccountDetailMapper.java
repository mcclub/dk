package com.dk.provider.user.mapper;

import com.dk.provider.user.entity.AccountDetail;

public interface AccountDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AccountDetail record);

    int insertSelective(AccountDetail record);

    AccountDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountDetail record);

    int updateByPrimaryKey(AccountDetail record);
}