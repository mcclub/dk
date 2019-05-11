package com.common.bean;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @Author ascme
 * @ClassName 封装的返回参数实体类
 * @Date 2019-02-19
 */
public class RestResult implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RestResult.class);
    private String respCode;

    private String respMsg;

    private Object data;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public RestResult() {

    }

    public RestResult setCodeAndMsg (String respCode,String respMsg) {
        this.respCode = respCode;
        this.respMsg = respMsg;
        this.data = new JSONObject();
        return this;
    }
}
