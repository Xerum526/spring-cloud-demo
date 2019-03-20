package com.spgcld.demo.seqid.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spgcld.demo.seqid.service.IdService;

@RestController
public class IdController {
	
	@Autowired
	private IdService idService;
	
	@RequestMapping("/getId")
	public String getId(@RequestParam("key") String key) throws Exception {
		return idService.newId(key);
	}
	
	@RequestMapping("/regist")
	public String regist(@RequestParam("key") String key, @RequestParam("idEigen") String idEigen, @RequestParam("initialSeqValue") String initialSeqValue) throws Exception {
		return idService.regist(key, idEigen, initialSeqValue, true);
	}

}
