package com.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author ascme
 * @ClassName 封装的公共代码控制类
 * @Date 2019-02-19
 */
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);


    /**
     * 公共返回参数格式
     * @param respCode 返回码
     * @param respMsg 返回描述
     * @param data 返回数据
     * @return 返回结果集
     */
    public RestResult getRestResult(String respCode, String respMsg, Object data){
        RestResult result = new RestResult();
        result.setRespCode(respCode);
        result.setRespMsg(respMsg);
        result.setData(data);
        return result;
    }

    public RestResult getSuccuss(JSONObject data){
        RestResult result = new RestResult();
        result.setRespCode(ResultEnume.SUCCESS);
        result.setRespMsg("成功");
        result.setData(data);
        return result;
    }

    public RestResult getFail(String data){
        RestResult result = new RestResult();
        result.setRespCode(ResultEnume.FAIL);
        result.setRespMsg("失败");
        result.setData(data);
        return result;
    }

    public RestResult getFail(){
        RestResult result = new RestResult();
        result.setRespCode(ResultEnume.FAIL);
        result.setRespMsg("失败");
        result.setData(null);
        return result;
    }

    public RestResult getFailRes(){
        RestResult result = new RestResult();
        result.setRespCode(ResultEnume.BUSY);
        result.setRespMsg("系统繁忙,请稍后再试");
        result.setData(null);
        return result;
    }


}
