package com.xetlab.leaderelection;

import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderElectionTest {

    private RedissonClient redissonClient;

    @Before
    public void setUp() throws Exception {
        Config config = Config.fromYAML(getClass().getResource("/redisson.yml"));
        redissonClient = Redisson.create(config);
    }

    @Test
    public void testElection() throws Exception {
        Map<String, Boolean> electionState = new ConcurrentHashMap<>();
        Map<String, LeaderElection> elections = new ConcurrentHashMap<>();
        for (int i = 0; i < 5; i++) {
            final Thread thread = new Thread("service" + i) {
                @Override
                public void run() {
                    LeaderElection leaderElection = new LeaderElection();
                    leaderElection.setRedissonClient(redissonClient);
                    leaderElection.tryHold("leader-lock");
                    elections.put(getName(), leaderElection);
                    while (true) {
                        if (leaderElection.isMaster()) {
                            System.out.println(getName() + ":" + leaderElection.isMaster());
                        }
                        electionState.put(getName(), leaderElection.isMaster());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            };
            thread.start();
        }

        waitAndQuit(electionState, elections);

        waitAndQuit(electionState, elections);

        waitAndQuit(electionState, elections);

        waitAndQuit(electionState, elections);

        waitAndQuit(electionState, elections);

        waitAndQuit(electionState, elections);

        Thread.sleep(3000);


        for (LeaderElection leaderElection : elections.values()) {
            leaderElection.shutdown();
        }
    }

    private void waitAndQuit(Map<String, Boolean> electionState, Map<String, LeaderElection> elections) throws Exception {
        Thread.sleep(3000);

        for (Map.Entry<String, Boolean> entry : electionState.entrySet()) {
            if (entry.getValue()) {
                elections.get(entry.getKey()).shutdown();
                return;
            }
        }
        System.out.println("no master");
    }

}
