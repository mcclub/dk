package com.dk.rest.user.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.AuthenUtils;
import com.common.utils.CommonUtils;
import com.common.utils.StringUtil;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.ICardInfoService;
import com.dk.rest.user.bean.CardInfoBeans;
import com.dk.rest.user.entity.CardInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 用户信用卡，储蓄卡信息管理
 */
@RestController
@RequestMapping("/cardInfo")
public class CardInfoController {
    private Logger logger = LoggerFactory.getLogger(CardInfoController.class);

    @Resource
    private ICardInfoService cardInfoServiceImpl;
    /*@Resource
    private RedisCacheServiceImpl redisCacheService;*/

    @RequestMapping("/add")
    public RestResult insert (@RequestBody CardInfoBean vo) {
        logger.info("banding card start 。。。");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(vo)) {
            if (!StringUtil.isNotEmpty(vo.getUserId())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空!");
            }
            if (StringUtil.isEmpty(vo.getRealName())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户姓名不能为空!");
            }
            if (StringUtil.isEmpty(vo.getCardCode())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"银行卡号不能为空!");
            }
            if (StringUtil.isEmpty(vo.getIdentity())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"身份证号不能为空!");
            }
            if (StringUtil.isEmpty(vo.getType())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"卡类型不能为空!");
            }
            if ((vo.getType()).equals("02")) {
                if (StringUtil.isEmpty(vo.getValid()) || StringUtil.isEmpty(vo.getCvv())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"有效期或安全码为空!");
                }
            }
            if (!StringUtil.isMobilePhone(vo.getPhone())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"手机号格式有误!");
            }
            /*if (!user.getVerificationCode().equals(redisCacheService.get(user.getPhone()))) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码输入有误！");
                return restResult;
            }*/


            //判断该卡是否已被绑定
            HashMap<String,Object> map = new HashMap<>();
            map.put("cardNo",vo.getCardCode());
            CardInfo bean = cardInfoServiceImpl.searchByNo(map);
            if (StringUtil.isNotEmpty(bean)) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"该卡已被绑定");
            } else {
                //银行卡四要素鉴权
                logger.info("银行卡四要素鉴权 。。。");
                HashMap<String,String> bodys = new HashMap<>();
                bodys.put("cardNo", vo.getCardCode());
                bodys.put("idNo", vo.getIdentity());
                bodys.put("name", vo.getRealName());
                bodys.put("phoneNo", vo.getPhone());
                String cardAuthenInfo = AuthenUtils.bankAuthen(bodys);
                JSONObject json = JSON.parseObject(cardAuthenInfo);
                String respCode = json.getString("respCode");
                if (respCode.equals("0001")) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"开户名不能为空");
                }
                if (respCode.equals("0002")) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"银行卡号格式错误");
                }
                if (respCode.equals("0003")) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"身份证号格式错误");
                }
                if (respCode.equals("0008")) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"信息不匹配");
                }
            /*if (!user.getVerificationCode().equals(redisCacheService.get(user.getPhone()))) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码输入有误！");
                return restResult;
            }*/
                if (respCode.equals("0000")) {
                    if (vo.getType().equals("01") && !json.getString("bankType").equals("借记卡")) {
                        return restResult.setCodeAndMsg(ResultEnume.FAIL,"请绑定储蓄卡");
                    }
                    if (vo.getType().equals("02") && !json.getString("bankType").equals("信用卡")) {
                        return restResult.setCodeAndMsg(ResultEnume.FAIL,"请绑定信用卡");
                    }
                    CardInfo cardInfo = new CardInfo();
                    BeanUtils.copyProperties(vo,cardInfo);
                    cardInfo.setType(json.getString("bankType").equals("借记卡")?"01":"02");
                    cardInfo.setBankCode(json.getString("bankCode"));
                    cardInfo.setBankName(json.getString("bankName"));
                    cardInfo.setCreateTime(new Date());
                    cardInfo.setUpdateTime(new Date());
                    cardInfo.setIsbind(1l);
                    try {
                        return cardInfoServiceImpl.bindingCard(cardInfo);
                    } catch (Exception e) {
                        return restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器内部错误");
                    }
                } else {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户信息验证失败");
                }
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误!");
        }
    }

    @RequestMapping("/offBinding")
    public RestResult offBinding (@RequestBody CardInfoBean vo) {
        logger.info("offBinding card start 。。。");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(vo)) {
            if (!StringUtil.isNotEmpty(vo.getUserId())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL," 用户id为空!");
            }
            if (StringUtil.isEmpty(vo.getCardCode())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"银行卡号为空!");
            }
            CardInfo cardInfo = new CardInfo();
            BeanUtils.copyProperties(vo,cardInfo);
            cardInfoServiceImpl.offBinding(cardInfo);
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"解绑成功!");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误!");
        }
    }

    @RequestMapping("/search")
    public RestResult search (@RequestBody Map map) throws Exception{
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (!StringUtil.isNotEmpty(map.get("userId"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空!");
            }
            if (!StringUtil.isNotEmpty(map.get("type"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"卡类型不能为空!");
            }
            List<CardInfo> cardInfoList = cardInfoServiceImpl.search(map);
            List<CardInfoBeans> cardInfoBeanList = new LinkedList<>();
            if(cardInfoList != null){
                for(CardInfo e: cardInfoList){
                    CardInfoBeans cardInfoBeans = new CardInfoBeans();
                    BeanUtils.copyProperties(e,cardInfoBeans);
                    CommonUtils.reflect(cardInfoBeans);
                    cardInfoBeanList.add(cardInfoBeans);
                }
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功!",cardInfoBeanList);
            }
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"查询结果为空!");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误!");
        }
    }
}
