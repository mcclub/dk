package com.dk.provider.basic.mapper;

import com.dk.provider.basic.entity.OemConfig;
import com.dk.provider.basis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;


@Mapper
public interface OemConfigMapper extends BaseMapper<OemConfig> {
    int deleteByPrimaryKey(Long id);

    int insert(OemConfig record);

    int insertSelective(OemConfig record);

    OemConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OemConfig record);

    int updateByPrimaryKey(OemConfig record);

    OemConfig searchByOemid(Map map);
}