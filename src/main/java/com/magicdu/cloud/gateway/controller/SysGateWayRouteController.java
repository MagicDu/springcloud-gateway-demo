package com.magicdu.cloud.gateway.controller;

import com.magicdu.cloud.gateway.entity.SysGateWayRoute;
import com.magicdu.cloud.gateway.service.SysGateWayRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * GateRoute
 */
@RestController
@RequestMapping("/gateway/api/v1")
public class SysGateWayRouteController {

    @Autowired
    private SysGateWayRouteService sysGateWayRouteService;


    @GetMapping("/getGateRouteList")
    public List<SysGateWayRoute> getGateRouteList(){
        return sysGateWayRouteService.getGateRouteList();
    }

    /**
     * 保存或者修改路由信息
     * @param sysGateWayRoute
     * @return
     */
    @PostMapping("/saveOrUpdateRoute")
    public Boolean addGateRoute(@RequestBody SysGateWayRoute sysGateWayRoute){
        sysGateWayRouteService.save(sysGateWayRoute);
        return true;
    }

    /**
     * 删除路由信息
     * @param sysGateWayRoute
     * @return
     */
    @PostMapping("/deleteRoute")
    public Boolean deleteGateRoute(@RequestBody SysGateWayRoute sysGateWayRoute){
        sysGateWayRouteService.delete(sysGateWayRoute);
        return true;
    }

}
