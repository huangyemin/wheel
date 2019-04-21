package com.xetlab.leaderelection;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LeaderElectionConfig {
    @Value("${spring.application.name}")
    private String appName;

    @Bean(destroyMethod = "shutdown")
    public LeaderElection leaderElection(RedissonClient redissonClient) {
        LeaderElection leaderElection = new LeaderElection();
        leaderElection.setRedissonClient(redissonClient);
        leaderElection.tryHold("leader-lock-" + appName);
        return leaderElection;
    }
}
