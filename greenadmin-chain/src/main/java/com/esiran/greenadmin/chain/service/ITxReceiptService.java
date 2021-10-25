package com.esiran.greenadmin.chain.service;

import com.esiran.greenadmin.chain.entity.BlockTx;
import com.esiran.greenadmin.chain.entity.TxReceipt;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
public interface ITxReceiptService extends IService<TxReceipt> {
    void insertReceipts(List<TxReceipt> receipts);
    void insertReceipt(TxReceipt receipt);
    TxReceipt getReceiptByTxHash(String hash);
    void remoteByTxHash(String hash);
}
