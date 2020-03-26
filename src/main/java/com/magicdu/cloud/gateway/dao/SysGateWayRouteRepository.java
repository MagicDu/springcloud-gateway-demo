package com.magicdu.cloud.gateway.dao;

import com.magicdu.cloud.gateway.entity.SysGateWayRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysGateWayRouteRepository extends JpaRepository<SysGateWayRoute,Integer> {
}
