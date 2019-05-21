package com.dk.provider.user.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.mapper.CardInfoMapper;
import com.dk.provider.user.service.ICardInfoService;
import com.dk.provider.user.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户卡信息管理
 */
@Service
public class CardInfoServiceImpl extends BaseServiceImpl<CardInfo> implements ICardInfoService {
    @Resource
    private CardInfoMapper cardInfoMapper;
    @Resource
    private IUserService userServiceImpl;


    @Override
    public CardInfo searchByNo(Map map) {
        return cardInfoMapper.searchByNo(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult bindingCard(CardInfo cardInfo) {
        RestResult restResult = new RestResult();
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",cardInfo.getUserId());
        map.put("cardNo",cardInfo.getCardCode());
        CardInfo bean = this.searchByNo(map);
        if (StringUtil.isNotEmpty(bean)) {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"同一张卡请勿重新绑定");
        } else {
            try {
                int num = cardInfoMapper.insert(cardInfo);
                if (num > 0) {
                    if (cardInfo.getType().equals("01")) {
                        HashMap<String,Object> updateMap = new HashMap<>();
                        updateMap.put("userId",cardInfo.getUserId());
                        updateMap.put("name",cardInfo.getRealName());
                        updateMap.put("identity",cardInfo.getIdentity());
                        userServiceImpl.updateByBindCard(updateMap);
                    }
                    return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"绑定成功");
                } else {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"绑定失败，请联系管理员");
                }
            } catch (Exception e) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器内部错误");
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int offBinding(CardInfo cardInfo) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("updateTime",new Date());
        map.put("userId",cardInfo.getUserId());
        map.put("cardNo",cardInfo.getCardCode());
        map.put("isbind",0l);
        return cardInfoMapper.offBinding(map);
    }

    @Override
    public RestResult search(Map map) {
        RestResult restResult = new RestResult();
        try {
            CardInfo cardInfo = cardInfoMapper.search(map);
            if (StringUtil.isNotEmpty(cardInfo)) {
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",cardInfo);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功");
            }

        } catch (Exception e) {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器内部错误");
        }
    }


}
