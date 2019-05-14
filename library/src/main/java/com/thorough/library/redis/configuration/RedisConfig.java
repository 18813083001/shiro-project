package com.thorough.library.redis.configuration;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
@ConditionalOnProperty(prefix = "redis",name = "redisCache")
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    private final RedisProperties jedisProperties;

    public RedisConfig(RedisProperties jedisProperties) {
        this.jedisProperties = jedisProperties;
    }

    @Bean(name="jedisPoolConfig")
    public JedisPoolConfig JedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(jedisProperties.getMaxIdle());
//        jedisPoolConfig.setMaxWaitMillis(10000);
        jedisPoolConfig.setMaxTotal(jedisProperties.getMaxTotal());
        jedisPoolConfig.setTestOnBorrow(true);
        return jedisPoolConfig;
    }

      //线上和本地单机版
    @Bean(name="jedisPool")
    @ConditionalOnProperty(prefix = "redis",name = "redisCache", havingValue = "single")
    public JedisPool jedisPool(JedisPoolConfig jedisPoolConfig){
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, jedisProperties.getHost(),jedisProperties.getPort());
        return jedisPool;
    }


      //测试环境集群
    @Bean(name="jedisSentinelPool")
    @ConditionalOnProperty(prefix = "redis",name = "redisCache", havingValue = "devCluster")
    public JedisSentinelPool jedisSentinelPool(JedisPoolConfig jedisPoolConfig){
        Set<String> sentinels = new HashSet<String>();
        sentinels.add(jedisProperties.getHost()+":"+ jedisProperties.getPort());
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(jedisProperties.getMaster(), sentinels, jedisPoolConfig);
        return jedisSentinelPool;
    }


      //线上集群
    @Bean(name="jedisCluster")
    @ConditionalOnProperty(prefix = "redis",name = "redisCache", havingValue = "onlineCluster")
    public JedisCluster jedisCluster(JedisPoolConfig jedisPoolConfig){
        Set<HostAndPort> nodes = new LinkedHashSet<HostAndPort>();
        nodes.add(new HostAndPort(jedisProperties.getHost(), jedisProperties.getPort()));
        JedisCluster cluster = new JedisCluster(nodes, jedisPoolConfig);
        return cluster;
    }

    //spring自带的redis 单机版
    @Bean(name="connectionFactory")
    @ConditionalOnProperty(prefix = "redis",name = "redisCache", havingValue = "springRedis")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig){
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        jedisConnectionFactory.setHostName(jedisProperties.getHost());
        jedisConnectionFactory.setPort(jedisProperties.getPort());
        return  jedisConnectionFactory;
    }

    //spring自带的redis 单机版
    @Bean(name="redisTemplate")
    @ConditionalOnProperty(prefix = "redis",name = "redisCache", havingValue = "springRedis")
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

    @Bean(name="stringRedisTemplate")
    @ConditionalOnProperty(prefix = "redis",name = "redisCache", havingValue = "springRedis")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory){
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(connectionFactory);
        return stringRedisTemplate;
    }


    //    @Bean(name="sentinelConfiguration") 集群的方式
//    public RedisSentinelConfiguration redisSentinelConfiguration(){
//        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
//        RedisNode.RedisNodeBuilder builder =  new RedisNode.RedisNodeBuilder();
////        builder.withName("master");
////        redisSentinelConfiguration.setMaster(builder.build());
////        RedisNode redisNode = new RedisNode("10.1.0.250",16800);
//        builder.withName("master6379");
//        redisSentinelConfiguration.setMaster(builder.build());
//        RedisNode redisNode = new RedisNode("192.168.31.101",26379);
//        Set sentinels = new LinkedHashSet();
//        sentinels.add(redisNode);
//        redisSentinelConfiguration.setSentinels(sentinels);
//        return redisSentinelConfiguration;
//    }

    //集群的方式
//    @Bean(name="connectionFactory")
//    public JedisConnectionFactory jedisConnectionFactory(RedisSentinelConfiguration sentinelConfiguration){
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration);
//        return  jedisConnectionFactory;
//    }



}
