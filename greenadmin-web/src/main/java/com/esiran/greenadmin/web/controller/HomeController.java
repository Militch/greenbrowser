package com.esiran.greenadmin.web.controller;

import com.esiran.greenadmin.chain.service.IBlockChainService;
import com.esiran.greenadmin.chain.service.impl.BlockChainServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final IBlockChainService service;

    public HomeController(IBlockChainService service) {
        this.service = service;
    }

    @GetMapping
    public String index() {
        return "index";
    }
}
