package com.dk.provider.repay.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.repay.entity.PaymentDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
}