package com.esiran.greenadmin.web.controller;

import com.esiran.greenadmin.chain.entity.LatestData;
import com.esiran.greenadmin.chain.service.IBlockChainService;
import com.esiran.greenadmin.web.entity.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private final IBlockChainService service;

    public HomeController(IBlockChainService service) {
        this.service = service;
    }
    @GetMapping("/status")
    public Status status() {
        Status status = new Status();
        status.setDifficulty(1L);
        status.setLatestHeight(1L);
        status.setBlockReward(1L);
        return status;
    }

    @GetMapping("/latest")
    public LatestData latest() {
        return service.getLatestData();
    }
}
