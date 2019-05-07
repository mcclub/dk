package com.dk.provider.plat.service;

import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.plat.entity.Subchannel;

import java.util.List;
import java.util.Map;

public interface SubchannelService extends BaseServiceI<Subchannel> {

    List<Subchannel> selectByrout(Map map) throws Exception;
}
