package com.dk.provider.plat.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.mapper.SubchannelMapper;
import com.dk.provider.plat.service.SubchannelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Service("subchannelService")
public class SubchannelServiceImpl extends BaseServiceImpl<Subchannel> implements SubchannelService {
    private Logger logger = LoggerFactory.getLogger(SubchannelServiceImpl.class);
    @Resource
    private SubchannelMapper subchannelMapper;
    @Resource
    public void setSqlMapper (SubchannelMapper subchannelMapper)
    {
        super.setBaseMapper (subchannelMapper);
    }

    /**
     * 根据大类通道id查询小类通道信息
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<Subchannel> selectByrout(Map map) throws Exception {
        Subchannel subchannel = subchannelMapper.selectByrout(map);
        if(subchannel != null){
            List<Subchannel> subchannelList = new LinkedList<>();
            subchannelList.add(subchannel);
            return  subchannelList;
        }

        return null;
    }

}
