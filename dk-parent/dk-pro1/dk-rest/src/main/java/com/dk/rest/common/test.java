package com.dk.rest.common;
import com.dk.provider.test.entity.InterfaceRecordEntity;
import com.dk.provider.test.service.InterfaceRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/test")
public class test {

    @Resource
    private InterfaceRecordService interfaceRecordService;

   /* @ResponseBody
    @RequestMapping(value = {"/inter"},method = RequestMethod.POST)*/
    //@Scheduled(cron = "0/5 * * * * ?")
    public void getInter() throws Exception{
        Map<String ,String > map = new HashMap<>();
        List<InterfaceRecordEntity> list = interfaceRecordService.queryList(map);
        System.out.println(list);
    }

}
