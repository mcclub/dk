package com.dk.provider.oem.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.oem.entity.OemInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;


@Mapper
public interface OemInfoMapper extends BaseMapper<OemInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(OemInfo record);

    int insertSelective(OemInfo record);

    OemInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OemInfo record);

    int updateByPrimaryKey(OemInfo record);

    OemInfo queryById(Map map);
}