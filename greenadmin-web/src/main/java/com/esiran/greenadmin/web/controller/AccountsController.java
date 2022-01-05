package com.esiran.greenadmin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esiran.greenadmin.chain.entity.Address;
import com.esiran.greenadmin.chain.entity.BlockTx;
import com.esiran.greenadmin.chain.service.IAddressService;
import com.esiran.greenadmin.chain.service.IBlockTxService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountsController {
    private final IAddressService service;
    private final IBlockTxService txService;
    public AccountsController(IAddressService service, IBlockTxService txService) {
        this.service = service;
        this.txService = txService;
    }

    @GetMapping
    public IPage<Address> list(@RequestParam(required = false,value = "p") Integer page){
        if (page == null || page.equals(0)){
            page = 1;
        }
        Page<Address> pg = new Page<>(page,20);
        return service.getListByPage(pg);
    }
    @GetMapping("/{address}")
    public Address item(@PathVariable String address) throws Exception {
        Address item = service.getAddress(address);
        if (item == null){
            throw new Exception("empty data");
        }
        return item;
    }
    @GetMapping("/{address}/txs")
    public IPage<BlockTx> item(
            @PathVariable String address,
            @RequestParam(required = false,value = "p") Integer page) throws Exception {
        if (page == null || page.equals(0)){
            page = 1;
        }
        Page<BlockTx> pg = new Page<>(page,20);
        return txService.getAddressTxsByPage(pg, address);
    }
}
