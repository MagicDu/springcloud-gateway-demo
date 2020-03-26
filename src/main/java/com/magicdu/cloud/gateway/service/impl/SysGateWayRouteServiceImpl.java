package com.magicdu.cloud.gateway.service.impl;

import com.magicdu.cloud.gateway.config.GatewayServiceHandler;
import com.magicdu.cloud.gateway.dao.SysGateWayRouteRepository;
import com.magicdu.cloud.gateway.entity.SysGateWayRoute;
import com.magicdu.cloud.gateway.service.SysGateWayRouteService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysGateWayRouteServiceImpl implements SysGateWayRouteService {

    @Autowired
    private SysGateWayRouteRepository sysGateWayRouteRepository;
    @Autowired
    private GatewayServiceHandler gatewayServiceHandler;

    /**
     * 保存路由信息
     *
     * @param sysGateWayRoute
     * @return
     */
    @Override
    public boolean save(SysGateWayRoute sysGateWayRoute) {
        sysGateWayRouteRepository.save(sysGateWayRoute);
        if(ObjectUtils.allNotNull(sysGateWayRoute.getId())){
           gatewayServiceHandler.update(sysGateWayRoute);
        }else{
            gatewayServiceHandler.saveRoute(sysGateWayRoute);
        }
        return true;
    }

    /**
     * 删除路由信息
     * @param sysGateWayRoute
     * @return
     */
    @Override
    public boolean delete(SysGateWayRoute sysGateWayRoute) {
        sysGateWayRouteRepository.delete(sysGateWayRoute);
        gatewayServiceHandler.deleteRoute(sysGateWayRoute.getServiceId());
        return true;
    }

    /**
     * 查询所有的路由信息
     *
     * @return
     */
    @Override
    public List<SysGateWayRoute> getGateRouteList() {
        return sysGateWayRouteRepository.findAll();
    }
}
