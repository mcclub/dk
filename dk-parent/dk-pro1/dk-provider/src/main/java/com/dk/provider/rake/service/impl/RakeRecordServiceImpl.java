package com.dk.provider.rake.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.rake.entity.FriendInfo;
import com.dk.provider.rake.entity.RakeRecord;
import com.dk.provider.rake.entity.RebateSum;
import com.dk.provider.rake.mapper.RakeRecordMapper;
import com.dk.provider.rake.service.IRakeRecordService;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 返佣记录
 */

@Service
public class RakeRecordServiceImpl extends BaseServiceImpl<RakeRecord> implements IRakeRecordService {
    private Logger logger = LoggerFactory.getLogger(RakeRecordServiceImpl.class);
    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
    private static List<Long> idList;

    @Resource
    private RakeRecordMapper rakeRecordMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public List<RakeRecord> query(Map map) throws Exception {
        return null;
    }

    @Override
    public int insert(RakeRecord rakeRecord) throws Exception {
        return 0;
    }

    @Override
    public int update(RakeRecord rakeRecord) throws Exception {
        return 0;
    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public RakeRecord queryByid(Long id) {
        return null;
    }





    @Override
    public RestResult queryRebate(Map map) {
        RestResult restResult = new RestResult();
        //计算今日返佣
        map.put("createTime",sdf.format(new Date()));
        double todayRebate = this.countRebate(map);
        //计算总返佣
        map.put("createTime",null);
        double allRebate = this.countRebate(map);
        int totalPeople = idList.size();
        RebateSum rebateSum = new RebateSum();
        rebateSum.setTodayRebate(todayRebate);
        rebateSum.setAllRebate(allRebate);
        rebateSum.setTotalPeople(totalPeople);
        return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",rebateSum);
    }


    @Override
    public double countRebate(Map map) {
        if (this.isVip(map)) {
            idList = userMapper.searchTwoThreeId(map);
        } else {
            idList = userMapper.searchTwoId(map);
        }
        if (StringUtil.isNotEmpty(idList) && idList.size()>0) {
            map.put("id",idList);
            return this.userRebate(map);
        } else {
            logger.info("您目前没有下级");
            return 0;
        }
    }


    @Override
    public double userRebate(Map map) {
        List<RakeRecord> rakeRecord = rakeRecordMapper.queryRakeRecord(map);
        if (StringUtil.isNotEmpty(rakeRecord)) {
            double count = 0;
            for (RakeRecord bean : rakeRecord) {
                count = count + Double.parseDouble(bean.getRokeAmt());
            }
            return count;
        } else {
            logger.info("佣金为0");
            return 0;
        }
    }


    @Override
    public boolean isVip(Map map) {
        User user = userMapper.searchUserById(map);
        if (StringUtil.isNotEmpty(user)) {
            if ((user.getClassId()).equals(4l) || (user.getClassId()).equals(5l) || (user.getClassId()).equals(6l)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    @Override
    public RestResult friendList(Map map) {
        RestResult restResult = new RestResult();
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        if (this.isVip(map)) {
            //查询一级好友
            idList = userMapper.searchTwoId(map);
            List<FriendInfo> firstFriend = this.searchNextFriend(idList,map);
            double firstFriendNum = this.countFriendRebate(firstFriend);
            resultMap.put("friendNum",firstFriend.size());
            resultMap.put("friendRebate",firstFriendNum);
            resultMap.put("list",firstFriend);
            result.add(resultMap);


            //查询二级好友
            Map<String,Object> resultMapTwo = new HashMap<>();
            idList = userMapper.searchThreeId(map);
            List<FriendInfo> secondFriend = this.searchNextFriend(idList,map);
            double secondFriendNum = this.countFriendRebate(secondFriend);
            resultMapTwo.put("friendNum",secondFriend.size());
            resultMapTwo.put("friendRebate",secondFriendNum);
            resultMapTwo.put("list",secondFriend);
            result.add(resultMapTwo);
        } else {
            idList = userMapper.searchTwoId(map);
            List<FriendInfo> firstFriend = this.searchNextFriend(idList,map);
            double firstFriendNum = this.countFriendRebate(firstFriend);
            resultMap.put("friendNum",firstFriend.size());
            resultMap.put("friendRebate",firstFriendNum);
            resultMap.put("list",firstFriend);
            result.add(resultMap);
        }
        return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",result);
    }

    public List<FriendInfo> searchNextFriend(List<Long> list, Map map) {
        if (StringUtil.isNotEmpty(list) && list.size()>0) {
            map.put("id",list);
            return rakeRecordMapper.friendList(map);
        } else {
            logger.info("您目前没有下级");
            return new ArrayList<>();
        }
    }


    public double countFriendRebate(List<FriendInfo> list) {
        if (StringUtil.isNotEmpty(list)) {
            double count = 0;
            for (FriendInfo bean : list) {
                bean.setPhone((bean.getPhone()).replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
                count = count + Double.parseDouble(bean.getRokeAmt());
            }
            return count;
        } else {
            logger.info("佣金为0");
            return 0;
        }
    }
}
