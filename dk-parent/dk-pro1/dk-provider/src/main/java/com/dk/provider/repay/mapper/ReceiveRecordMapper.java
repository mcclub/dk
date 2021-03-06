package com.dk.provider.repay.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.repay.entity.ReceiveRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReceiveRecordMapper extends BaseMapper<ReceiveRecord> {
    int deleteByPrimaryKey(Long id);

    int insert(ReceiveRecord record);

    int insertSelective(ReceiveRecord record);

    ReceiveRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReceiveRecord record);

    int updateByPrimaryKey(ReceiveRecord record);

    int updateStates(ReceiveRecord record);

    /**
     * 查询
     * @param map
     * @return
     */
    List<ReceiveRecord> historyQuery(Map map) ;


    /**
     * 统计
     * @param map
     * @return
     */
    int historyCounts(Map<String, Object> map)  ;
}