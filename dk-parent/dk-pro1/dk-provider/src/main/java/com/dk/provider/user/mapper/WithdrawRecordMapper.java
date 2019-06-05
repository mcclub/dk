package com.dk.provider.user.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.user.entity.WithdrawRecord;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface WithdrawRecordMapper extends BaseMapper<WithdrawRecord> {
    int deleteByPrimaryKey(Long id);

    int insert(WithdrawRecord record);

    int insertSelective(WithdrawRecord record);

    WithdrawRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WithdrawRecord record);

    int updateByPrimaryKey(WithdrawRecord record);
}