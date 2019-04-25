package com.dk.provider.test.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.test.entity.InterfaceRecordEntity;
import com.dk.provider.test.mapper.InterfaceRecordMapper;
import com.dk.provider.test.service.InterfaceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service("interfaceRecordService")
public class InterfaceRecordServiceImpl extends BaseServiceImpl<InterfaceRecordEntity> implements InterfaceRecordService {
    private static Logger logger = LoggerFactory.getLogger(InterfaceRecordServiceImpl.class);

    @Resource
    private InterfaceRecordMapper interfaceRecordMapper;

    @Resource
    public void setSqlMapper (InterfaceRecordMapper interfaceRecordMapper)
    {
        super.setBaseMapper (interfaceRecordMapper);
    }

    //private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 查询
     * @param map 参数map
     * @return
     * @throws Exception
     */
    @Override
    public List<InterfaceRecordEntity> queryList(Map map) throws Exception {
        logger.info("进入queryList方法,接收参数为:{}",map);
        try{
            if(!map.isEmpty()){
                List<InterfaceRecordEntity> replaceEntityList = interfaceRecordMapper.query(map);
                logger.info("replaceEntityList的值为:{}",replaceEntityList);

                if(replaceEntityList != null){
                    return replaceEntityList;
                }else{
                    return null;
                }
            }else{
                logger.error("map不能为空");
                throw new NullPointerException("map不能为空");
            }
        }catch (Exception e){
            logger.error("出错了！Error:{}",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 添加
     * @param entity 参数实体
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(InterfaceRecordEntity entity) throws Exception {
        logger.info("进入insert方法,接收参数为:{}",entity);
        try{
            if(entity != null){
                int id = interfaceRecordMapper.insert(entity);//平台订单号
                logger.info("平台订单号id:{}",id);
                if(id > 0){
                    return id;
                }else{
                    return 0;
                }
            }
            return 0;
        }catch (Exception e){
            logger.error("出错了,Error:{}",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 修改
     * @param entity 参数实体
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(InterfaceRecordEntity entity) throws Exception {
        logger.info("进入update方法,接收参数为:{}",entity);
        try{
            if(entity != null){
                int id = interfaceRecordMapper.update(entity);//平台订单号
                logger.info("平台订单号id:{}",id);
                if(id > 0){
                    return id;
                }else{
                    return 0;
                }
            }
            return 0;
        }catch (Exception e){
            logger.error("出错了,Error:{}",e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
