package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.entity.BlockTx;
import com.esiran.greenadmin.chain.entity.TxReceipt;
import com.esiran.greenadmin.chain.mapper.TxReceiptMapper;
import com.esiran.greenadmin.chain.service.ITxReceiptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
@Service
public class TxReceiptServiceImpl extends ServiceImpl<TxReceiptMapper, TxReceipt> implements ITxReceiptService {

    @Override
    public void insertReceipts(List<TxReceipt> receipts) {
        if (receipts == null || receipts.size() <= 0){
            return;
        }
        for (TxReceipt receipt : receipts) {
            insertReceipt(receipt);
        }
    }

    @Override
    public void insertReceipt(TxReceipt receipt) {
        if (receipt == null){
            return;
        }
        save(receipt);
    }

    @Override
    public TxReceipt getReceiptByTxHash(String hash) {
        Wrapper<TxReceipt> wrapper = new LambdaQueryWrapper<TxReceipt>().eq(TxReceipt::getTxHash, hash);
        return getOne(wrapper);
    }

    @Override
    public void remoteByTxHash(String hash) {
        Wrapper<TxReceipt> wrapper = new LambdaQueryWrapper<TxReceipt>().eq(TxReceipt::getTxHash, hash);
        this.remove(wrapper);
    }
}
