package com.magicdu.cloud.gateway.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Set;


@EnableCaching
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConfigurationProperties(prefix = "spring.redis.sentinel")
public class RedisConfig {

    /**
     * redis配置 
     * @return
     */
    private Set<String> nodes;
    private String master;

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private long timeout;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private long maxWait;
    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxActive;

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        //连接池通用配置
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);

        LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
	            .poolConfig(genericObjectPoolConfig)
	            .commandTimeout(Duration.ofSeconds(timeout))
	            .build();

    	if(master.equals("")) {
    	    //基本配置
            RedisStandaloneConfiguration redisSentinelConfiguration = new RedisStandaloneConfiguration();
            redisSentinelConfiguration.setHostName(host);
            redisSentinelConfiguration.setPort(port);
            redisSentinelConfiguration.setDatabase(database);
            if (!password.equals("")) {
                RedisPassword redisPassword = RedisPassword.of(password);
                redisSentinelConfiguration.setPassword(redisPassword);
            }

            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration,lettuceClientConfiguration);
            //connectionFactory.afterPropertiesSet();

            return connectionFactory;
    	}else {
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration(master, nodes);
            redisSentinelConfiguration.setPassword(RedisPassword.of(password.toCharArray()));
            if(0==database) {
            	database = 1;
            }
            redisSentinelConfiguration.setDatabase(database);

            return new LettuceConnectionFactory(redisSentinelConfiguration, lettuceClientConfiguration);
    	}
    }


    /**
             * 配置自定义redisTemplate
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactoryLet) {
    	RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactoryLet);
        template.setValueSerializer(jackson2JsonRedisSerializer());
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * json序列化
     * @return
     */
    @Bean
    public RedisSerializer<Object> jackson2JsonRedisSerializer() {
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        return serializer;
    }


    /**
     * 配置缓存管理器
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存的默认过期时间，也是使用Duration设置
        config = config.entryTtl(Duration.ofDays(1))
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer()))
                // 不缓存空值
                .disableCachingNullValues();
        // 使用自定义的缓存配置初始化一个cacheManager
        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                // 一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .build();
        return cacheManager;
    }


}
