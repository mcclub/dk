package com.dk.provider.user.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.mapper.CardInfoMapper;
import com.dk.provider.user.mapper.UserMapper;
import com.dk.provider.user.service.ICardInfoService;
import com.dk.provider.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户卡信息管理
 */
@Service
public class CardInfoServiceImpl extends BaseServiceImpl<CardInfo> implements ICardInfoService {
    private Logger logger = LoggerFactory.getLogger(CardInfoServiceImpl.class);
    @Resource
    private CardInfoMapper cardInfoMapper;
    @Resource
    private IUserService userServiceImpl;
    @Resource
    private UserMapper userMapper;


    @Override
    public CardInfo searchByNo(Map map) {
        return cardInfoMapper.searchByNo(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult bindingCard(CardInfo cardInfo) {
        RestResult restResult = new RestResult();
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
                Map<String,Object> map = new HashMap<>();
                map.put("userId",cardInfo.getUserId());
                Long parentId = userMapper.searchParent(map);
                if (parentId != null) {
                    try {
                        User user = userServiceImpl.queryByid(parentId);
                        if (user != null) {
                            if (user.getClassId() == 1l || user.getClassId() == 2l) {
                                //查询直接推荐的且已激活的人数
                                int referNum = userMapper.bindNumByUserId(user.getId());
                                if (referNum == 5) {
                                    Map<String,Object> parm = new HashMap<>();
                                    parm.put("userId",user.getId());
                                    parm.put("classId",2l);
                                    parm.put("className","黄金");
                                    userMapper.updateUserClass(map);
                                } else if (referNum == 10) {
                                    Map<String,Object> parm = new HashMap<>();
                                    parm.put("userId",user.getId());
                                    parm.put("classId",3l);
                                    parm.put("className","钻石");
                                    userMapper.updateUserClass(map);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.info("上级用户升级失败");
                        logger.info("错误信息="+e.getMessage());
                    }
                } else {
                    logger.info("该用户没有推荐人");
                }
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"绑定成功");
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"绑定失败，请联系管理员");
            }
        } catch (Exception e) {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器内部错误");
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
    public List<CardInfo> search(Map map) {
        try {
            List<CardInfo> cardInfo = cardInfoMapper.search(map);
            if (StringUtil.isNotEmpty(cardInfo)) {
                return cardInfo;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setbill(Map map) {
        return cardInfoMapper.setbill(map);
    }


}
