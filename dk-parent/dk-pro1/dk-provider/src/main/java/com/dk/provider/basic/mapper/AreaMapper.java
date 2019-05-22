package com.dk.provider.basic.mapper;

import com.dk.provider.basic.entity.Area;
import com.dk.provider.basis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {
    int insert(Area record);

    int insertSelective(Area record);

    List<Area> queryList(Map map);
}