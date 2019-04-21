# 有用的轮子

## 主从选举
基于redisson的RLock实现的主从选举（leader election）
spring boot中的使用示例：
@Value("${spring.application.name}")
private String appName;

@Bean(destroyMethod = "shutdown")
public LeaderElection leaderElection(RedissonClient redissonClient) {
    LeaderElection leaderElection = new LeaderElection();
    leaderElection.setRedissonClient(redissonClient);
    leaderElection.tryHold("leader-lock-" + appName);
    return leaderElection;
}
