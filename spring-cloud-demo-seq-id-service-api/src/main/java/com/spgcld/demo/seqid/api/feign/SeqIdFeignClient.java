package com.spgcld.demo.seqid.api.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = SeqIdFeignConstants.FEIGN_NAME, fallback = SeqIdFeignClient.SeqIdFeignClientHystrixFallBack.class)
public interface SeqIdFeignClient {
	
	@RequestMapping(value = "/regist")
	public String regist(@RequestParam("key") String key, @RequestParam("idEigen") String idEigen, @RequestParam("initialSeqValue") String initialSeqValue) throws Exception;
	
	@RequestMapping(value = "/getId")
	public String getId(@RequestParam("key") String key) throws Exception;
		
	public abstract class SeqIdFeignClientHystrixFallBack implements SeqIdFeignClient{
		
	}

}
