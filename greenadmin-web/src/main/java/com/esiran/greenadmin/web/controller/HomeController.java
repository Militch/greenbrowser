package com.esiran.greenadmin.web.controller;

import com.esiran.greenadmin.chain.entity.*;
import com.esiran.greenadmin.chain.service.IBlockChainService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class HomeController {
    private final IBlockChainService chainService;
    public HomeController(IBlockChainService chainService) {
        this.chainService = chainService;
    }
    @GetMapping("/status")
    public ChainStatus status() {
        return chainService.getChainStatus();
    }

    @GetMapping("/search")
    public SearchResult search(@RequestParam(value = "q") String q){
        return chainService.search(q);
    }

    @GetMapping("/latest")
    public LatestData latest() {
        return chainService.getLatestData();
    }
}
