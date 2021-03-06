package com.spgcld.demo.seqid.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeqIdConfiguration {
	
	@Value("${seq-id.idSyncThreadPoolExecutor.corePoolSize}")
	public String corePoolSize;
	
	@Value("${seq-id.idSyncThreadPoolExecutor.maximumPoolSize}")
	public String maximumPoolSize;
	
	@Value("${seq-id.idSyncThreadPoolExecutor.keepAliveSeconds}")
	public String keepAliveSeconds;
	
	@Value("${seq-id.idSyncThreadPoolExecutor.workQueue.workQueueCapacity}")
	public String workQueueCapacity;
	
	@Value("${seq-id.redis.cluster.nodes}")
	public String redisClusterNodes;
		
	@Value("${seq-id.defaultBreakPointFileBasePath}")
	public String defaultBreakPointFileBasePath;
	
	@Value("${seq-id.defaultStandbyIdSyncQueueSizeMax}")
	public String defaultStandbyIdSyncQueueSizeMax;
	
	@Value("${seq-id.zkHost}")
	public String zkHost;

	@Value("${seq-id.zkSeqIdBaseKeyPath}")
	public String zkSeqIdBaseKeyPath;

}
