package com.dk.rest.user.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.AuthenUtils;
import com.common.utils.StringUtil;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.ICardInfoService;
import com.dk.rest.user.entity.CardInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;

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
            /*if (StringUtil.isEmpty(vo.getType())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"卡类型不能为空!");
            }*/
            if (!StringUtil.isMobilePhone(vo.getPhone())) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"手机号格式有误!");
            }
            /*if (!user.getVerificationCode().equals(redisCacheService.get(user.getPhone()))) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码输入有误！");
                return restResult;
            }*/
            if (vo.getType().equals("01")) {
                if (StringUtil.isEmpty(vo.getBankName())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"银行名称不能为空!");
                }
            }
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
            }
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"成功");
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

    public static void main(String[] args) {
        HashMap<String,String> bodys = new HashMap<>();
        bodys.put("cardNo", "6216913101638113");
        bodys.put("idNo", "430703199712174518");
        bodys.put("name", "张立昌");
        bodys.put("phoneNo", "15210243233");
        String cardAuthenInfo = AuthenUtils.bankAuthen(bodys);
        System.out.println("cardAuthenInfo="+cardAuthenInfo);
        //String cardAuthenInfo = "{\"name\":\"张立昌\",\"cardNo\":\"6216913101638113\",\"idNo\":\"430703199712174518\",\"phoneNo\":\"15210243233\",\"respMessage\":\"信息匹配\",\"respCode\":\"0000\",\"bankName\":\"中国民生银行\",\"bankKind\":\"借记卡普卡\",\"bankType\":\"借记卡\",\"bankCode\":\"CMBC\"}";
        JSONObject json = JSON.parseObject(cardAuthenInfo);
        String respCode = json.getString("respCode");
        System.out.println(respCode);
    }
}
