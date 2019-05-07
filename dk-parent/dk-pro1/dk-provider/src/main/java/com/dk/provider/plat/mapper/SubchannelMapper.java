package com.dk.provider.plat.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.plat.entity.Subchannel;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SubchannelMapper extends BaseMapper<Subchannel> {
    int deleteByPrimaryKey(Long id);

    int insert(Subchannel record);

    int insertSelective(Subchannel record);

    Subchannel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Subchannel record);

    int updateByPrimaryKey(Subchannel record);

    Subchannel selectByrout(Map map);
}