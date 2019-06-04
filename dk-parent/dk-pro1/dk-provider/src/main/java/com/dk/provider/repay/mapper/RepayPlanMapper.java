package com.dk.provider.repay.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.repay.entity.RepayPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface RepayPlanMapper extends BaseMapper<RepayPlan> {
    int deleteByPrimaryKey(Long id);

    int insert(RepayPlan record);

    int insertSelective(RepayPlan record);

    RepayPlan selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepayPlan record);

    int updateByPrimaryKey(RepayPlan record);

    /**
     * 查询代还
     * @param map
     * @return
     */
    RepayPlan queryPlan(Map map);

    /**
     * 更新代还详情为立即执行状态
     * @param map
     * @return
     */
    int activeDetail(Map map);


    /**
     * 更新代还汇总为立即执行状态
     * @param map
     * @return
     */
    int activePlan(Map map);
}