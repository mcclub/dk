package com.dk.provider.plat.mapper;

import com.dk.provider.plat.entity.Subchannel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubchannelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Subchannel record);

    int insertSelective(Subchannel record);

    Subchannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Subchannel record);

    int updateByPrimaryKey(Subchannel record);
}