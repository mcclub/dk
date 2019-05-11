package com.dk.provider.user.service;

import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.user.entity.CardInfo;

import java.util.Map;

public interface ICardInfoService extends BaseServiceI<CardInfo> {
    /**
     * 通过卡号查询是否已绑定
     * @param map
     * @return
     */
    CardInfo searchByNo (Map map);

    /**
     * 绑卡
     * @param cardInfo
     * @return
     */
    RestResult bindingCard (CardInfo cardInfo);

    /**
     * 卡解绑
     * @param  cardInfo
     * @return
     */
    int offBinding(CardInfo cardInfo);
}
