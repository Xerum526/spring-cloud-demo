package com.spgcld.demo.webfront.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/**
 * 定义步长的轮询策略
 * @author wuyichao
 *
 */
public class RoundStepLoadBalanceRule extends AbstractLoadBalancerRule{
	
	private AtomicInteger nextServerCyclicCounter;
	
	private static final int DEFAULT_ROUND_STEP = 3;
	
	public RoundStepLoadBalanceRule() {
		nextServerCyclicCounter = new AtomicInteger(0);
	}

	@Override
	public Server choose(Object key) {
		
		ILoadBalancer lb = getLoadBalancer();
		
		if(lb == null) {
			System.out.println("no load balancer");
			return null;
		}
		
		Server server = null;
        int count = 0;
        while (server == null && count++ < 10) {
            List<Server> reachableServers = lb.getReachableServers();
            List<Server> allServers = lb.getAllServers();
            int upCount = reachableServers.size();
            int serverCount = allServers.size();

            if ((upCount == 0) || (serverCount == 0)) {
            	System.out.println("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount, DEFAULT_ROUND_STEP);
            server = allServers.get(nextServerIndex);

            if (server == null) {
                Thread.yield();
                continue;
            }

            if (server.isAlive() && (server.isReadyToServe())) {
                return (server);
            }
            server = null;
        }

        if (count >= 10) {
            System.out.println("No available alive servers after 10 tries from load balancer: " + lb);
        }
        return server;
		
	}
	
	 private int incrementAndGetModulo(int modulo, int roundStep) {
	        for (;;) {
	            int current = nextServerCyclicCounter.get();
	            int next = ((current + 1) / roundStep) % modulo;
	            if (nextServerCyclicCounter.compareAndSet(current, ++current))
	                return next;
	        }
	    }

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
		
		
	}
	
}
