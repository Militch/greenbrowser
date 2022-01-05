package com.esiran.greenadmin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.chain.entity.Block;
import com.esiran.greenadmin.chain.entity.BlockDTO;
import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.service.IBlockChainService;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/blocks")
public class BlocksController {
    private final IBlockChainService chainService;

    public BlocksController(IBlockChainService chainService) {
        this.chainService = chainService;
    }

    @GetMapping
    public IPage<BlockHeader> blocks(@RequestParam(required = false,value = "p") Integer page){
        if (page == null || page.equals(0)){
            page = 1;
        }
        Page<BlockHeader> pg = new Page<>(page,20);
        return chainService.getBlockHeadersByPage(pg);
    }
    @GetMapping("/{hash}")
    public Block block(@PathVariable String hash) throws Exception {
        Pattern pattern = Pattern.compile("^0x[0-9A-Za-z]{64}$");
        Matcher m = pattern.matcher(hash);
        if (!m.matches()){
            throw new Exception("err");
        }
        Block block = chainService.getBlockByHash(hash);
        if (block == null){
            throw new Exception("empty data");
        }
        return block;
    }
}
