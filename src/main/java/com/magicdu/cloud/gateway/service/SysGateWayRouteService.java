package com.magicdu.cloud.gateway.service;

import com.magicdu.cloud.gateway.entity.SysGateWayRoute;

import java.util.List;

public interface SysGateWayRouteService {
    /**
     * 保存路由信息
     * @param sysGateWayRoute
     * @return
     */
    boolean save(SysGateWayRoute sysGateWayRoute);

    /**
     * 删除路由信息
     * @param sysGateWayRoute
     * @return
     */
    boolean delete(SysGateWayRoute sysGateWayRoute);

    /**
     * 查询所有的路由信息
     * @return
     */
    List<SysGateWayRoute> getGateRouteList();
}
