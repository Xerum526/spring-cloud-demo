package com.spgcld.demo.fallback;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.seq.id.DefaultAtomicLongSeq;
import com.seq.id.DefaultIdFactory;
import com.seq.id.DefaultIdSyncStrategy;
import com.seq.id.Id;
import com.spgcld.demo.config.SeqIdConfig;
import com.spgcld.demo.seqid.api.feign.SeqIdFeignClient.SeqIdFeignClientHystrixFallBack;

@Component
public class SeqIdFeignClientFallBack extends SeqIdFeignClientHystrixFallBack implements InitializingBean{
	
	private Id id;
	
	@Resource
	private SeqIdConfig seqIdConfig;

	@Override
	public String regist(String key, String idEigen, String initialSeqValue) throws Exception {
		if (StringUtils.isEmpty(initialSeqValue)) {
			initialSeqValue = "0";
		}
		if (StringUtils.isEmpty(idEigen)) {
			idEigen = "";
		}
		idEigen += "x";
		long initialSeqValueLong = Long.parseLong(initialSeqValue);
		id.registIdFactory(key, new DefaultIdFactory(idEigen, new DefaultAtomicLongSeq(key, initialSeqValueLong), 10000,
				4000, 1, TimeUnit.MILLISECONDS));
		id.registIdSyncStrategy(key, new DefaultIdSyncStrategy(key, null));
		return key;
	}

	@Override
	public String getId(String key) throws Exception {
		String idValue = id.newIdByQueue(key);
		if(idValue == null) {
			regist(key, key, null);
		}
		return id.newIdByQueue(key);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Properties extProperties = new Properties();
		extProperties.put("idSyncThreadPoolExecutor.corePoolSize", seqIdConfig.corePoolSize);
		extProperties.put("idSyncThreadPoolExecutor.maximumPoolSize", seqIdConfig.maximumPoolSize);
		extProperties.put("idSyncThreadPoolExecutor.keepAliveSeconds", seqIdConfig.keepAliveSeconds);
		extProperties.put("idSyncThreadPoolExecutor.workQueue.workQueueCapacity", seqIdConfig.workQueueCapacity);
		extProperties.put("seq-id.redis.cluster.nodes", seqIdConfig.redisClusterNodes);
		extProperties.put("default.breakPointFileBasePath", seqIdConfig.defaultBreakPointFileBasePath);
		extProperties.put("default.standbyIdSyncQueueSize.max", seqIdConfig.defaultStandbyIdSyncQueueSizeMax);
		id = Id.getInstance(extProperties);		
	}

}
