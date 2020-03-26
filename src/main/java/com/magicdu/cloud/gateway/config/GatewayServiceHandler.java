package com.magicdu.cloud.gateway.config;

import com.magicdu.cloud.gateway.dao.SysGateWayRouteRepository;
import com.magicdu.cloud.gateway.entity.SysGateWayRoute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由信息公共处理
 */
@Slf4j
@Service
public class GatewayServiceHandler implements ApplicationEventPublisherAware, CommandLineRunner {
    @Autowired
    private RedisRouteDefinitionRepository routeDefinitionWriter;
    private ApplicationEventPublisher publisher;

    @Autowired
    private SysGateWayRouteRepository repository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        loadRouteConfigFromDataBase();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    /**
     * load route info from database to redis then publish
     * @return
     */
    private void loadRouteConfigFromDataBase() {
        // 从redis  删除之前的路由信息
        redisTemplate.delete(RedisRouteDefinitionRepository.GATEWAY_ROUTES);
        List<SysGateWayRoute> routeList = repository.findAll();
        routeList.forEach(route -> {
            RouteDefinition definition = handleData(route);
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        });
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("=======网关配置信息===加载完成======");
    }


    /**
     * save route info
     * @param gatewayRoute
     */
    public void saveRoute(SysGateWayRoute gatewayRoute){
        RouteDefinition definition=handleData(gatewayRoute);
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     *  update route info
     * @param gatewayRoute
     */
    public void update(SysGateWayRoute gatewayRoute) {
        RouteDefinition definition=handleData(gatewayRoute);
        try {
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete route info
     * @param routeId
     */
    public void deleteRoute(String routeId){
        routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }


    /**
     * route data handel an convert
     *
     * @param gatewayRoute
     * @return
     */
    private RouteDefinition handleData(SysGateWayRoute gatewayRoute) {
        RouteDefinition definition = new RouteDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        PredicateDefinition predicate = new PredicateDefinition();
        FilterDefinition filterDefinition = new FilterDefinition();
        Map<String, String> filterParams = new HashMap<>(8);

        URI uri = null;
        if (gatewayRoute.getUri().startsWith("http")) {
            //http address
            uri = UriComponentsBuilder.fromHttpUrl(gatewayRoute.getUri()).build().toUri();
        } else {
            //register center
            uri = UriComponentsBuilder.fromUriString("lb://" + gatewayRoute.getUri()).build().toUri();
        }

        definition.setId(gatewayRoute.getServiceId());
        // 名称是固定的，spring gateway会根据名称找对应的PredicateFactory
        predicate.setName("Path");
        predicateParams.put("pattern", gatewayRoute.getPredicates());
        predicate.setArgs(predicateParams);
        // 名称是固定的, 路径去前缀
        filterDefinition.setName("StripPrefix");
        filterParams.put("_genkey_0", gatewayRoute.getFilters().toString());
        filterDefinition.setArgs(filterParams);
        definition.setPredicates(Arrays.asList(predicate));
        definition.setFilters(Arrays.asList(filterDefinition));
        definition.setUri(uri);
        definition.setOrder(Integer.parseInt(gatewayRoute.getSort()));
        return definition;
    }

}
