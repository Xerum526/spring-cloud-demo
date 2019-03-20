package com.spgcld.demo.seqid.service;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.seq.id.DefaultAtomicLongSeq;
import com.seq.id.DefaultIdFactory;
import com.seq.id.DefaultIdSyncStrategy;
import com.seq.id.Id;
import com.spgcld.demo.seqid.config.SeqIdConfiguration;

@Service
public class IdService implements InitializingBean {

	private Id id;

	private CuratorFramework zkCuratorFramework;

	@Resource
	private SeqIdConfiguration seqIdConfiguration;
	
	public String newId(String key) throws Exception {
		return id.newIdByQueue(key);
	}

	public String regist(String key, String idEigen, String initialSeqValue, boolean addKeyToZK) throws Exception {
		if (StringUtils.isEmpty(initialSeqValue)) {
			initialSeqValue = "0";
		}
		if (StringUtils.isEmpty(idEigen)) {
			idEigen = "";
		}
		long initialSeqValueLong = Long.parseLong(initialSeqValue);
		id.registIdFactory(key, new DefaultIdFactory(idEigen, new DefaultAtomicLongSeq(key, initialSeqValueLong), 10000,
				4000, 1, TimeUnit.MILLISECONDS));
		id.registIdSyncStrategy(key, new DefaultIdSyncStrategy(key, null));
		if (!addKeyToZK) {
			return initialSeqValue;
		}
		String keyPath = seqIdConfiguration.zkSeqIdBaseKeyPath + "/" + key;
		Stat stat = zkCuratorFramework.checkExists().forPath(keyPath);
		if (stat == null) {
			zkCuratorFramework.create()
			.creatingParentsIfNeeded()
			.withMode(CreateMode.PERSISTENT)
			.forPath(keyPath, idEigen.getBytes(Charset.defaultCharset()));
		}
		return initialSeqValue;
	}

	private void createZKCuratorFramework() {
		zkCuratorFramework = CuratorFrameworkFactory.builder()
				.connectString(seqIdConfiguration.zkHost)
				.retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
				.connectionTimeoutMs(10000)
				.sessionTimeoutMs(10000).build();
		zkCuratorFramework.start();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		createZKCuratorFramework();
		Properties extProperties = new Properties();
		extProperties.put("idSyncThreadPoolExecutor.corePoolSize", seqIdConfiguration.corePoolSize);
		extProperties.put("idSyncThreadPoolExecutor.maximumPoolSize", seqIdConfiguration.maximumPoolSize);
		extProperties.put("idSyncThreadPoolExecutor.keepAliveSeconds", seqIdConfiguration.keepAliveSeconds);
		extProperties.put("idSyncThreadPoolExecutor.workQueue.workQueueCapacity", seqIdConfiguration.workQueueCapacity);
		extProperties.put("seq-id.redis.cluster.nodes", seqIdConfiguration.redisClusterNodes);
		extProperties.put("default.breakPointFileBasePath", seqIdConfiguration.defaultBreakPointFileBasePath);
		extProperties.put("default.standbyIdSyncQueueSize.max", seqIdConfiguration.defaultStandbyIdSyncQueueSizeMax);
		id = Id.getInstance(extProperties);
		Stat stat = zkCuratorFramework.checkExists().forPath(seqIdConfiguration.zkSeqIdBaseKeyPath);
		if (stat == null) {
			zkCuratorFramework.create()
			.creatingParentsIfNeeded()
			.withMode(CreateMode.PERSISTENT)
			.forPath(seqIdConfiguration.zkSeqIdBaseKeyPath);
			return;
		}
		List<String> list = zkCuratorFramework.getChildren().forPath(seqIdConfiguration.zkSeqIdBaseKeyPath);
		if (list != null && !list.isEmpty()) {
			byte[] idEigrnByteAry;
			String idEigen;
			for (String key : list) {
				idEigrnByteAry = zkCuratorFramework.getData().forPath(seqIdConfiguration.zkSeqIdBaseKeyPath + "/" + key);
				idEigen = new String(idEigrnByteAry, Charset.defaultCharset());
				regist(key, idEigen, null, false);
			}
		}
	}

}
