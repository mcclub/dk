package com.dk.provider.classI.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.classI.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(ClassInfo record);

    int insertSelective(ClassInfo record);

    ClassInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClassInfo record);

    int updateByPrimaryKey(ClassInfo record);
}