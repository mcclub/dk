package com.dk.provider.user.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.user.entity.CardInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CardInfoMapper extends BaseMapper<CardInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(CardInfo record);

    int insertSelective(CardInfo record);

    CardInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CardInfo record);

    int updateByPrimaryKey(CardInfo record);

    CardInfo queryByuserId(Map map);

    /**
     * 通过卡号查询是否已绑定
     * @param map
     * @return
     */
    CardInfo searchByNo (Map map);

    /**
     * 卡解绑
     * @param map
     * @return
     */
    int offBinding(Map map);

    /**
     * 查询用户名下的行用卡／储蓄卡
     * @param map
     * @return
     */
    List<CardInfo> search (Map map);

    int setbill(Map map);
}