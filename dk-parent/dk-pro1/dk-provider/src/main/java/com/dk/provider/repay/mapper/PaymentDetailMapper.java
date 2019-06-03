package com.dk.provider.repay.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.repay.entity.PaymentDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PaymentDetailMapper extends BaseMapper<PaymentDetail> {
    int deleteByPrimaryKey(Long id);

    int insert(PaymentDetail record);

    int insertSelective(PaymentDetail record);

    PaymentDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentDetail record);

    int updateByPrimaryKey(PaymentDetail record);

    /**
     * 批量新增
     * @param list
     * @return
     */
    int insertList (List<PaymentDetail> list);


    /**
     * 查询未执行的订单
     * @param map
     * @return
     */
    List<PaymentDetail> searchNotPerformed(Map map);

    /**
     * 更新计划状态
     * @param list
     * @return
     */
    int updatePantStatus(List<Long> list);

    /**
     * 更新详情状态
     * @param list
     * @return
     */
    int updateDetailStatus(List<Long> list);

    /**
     * 查询计划下的详情是否执行完
     * @param map
     * @return
     */
    List<PaymentDetail> searchDetailIfFinish(Map map);

    /**
     * 更新计划的状态
     * @param map
     * @return
     */
    int updatePlantFinish(Map map);

}