package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.esiran.greenadmin.chain.entity.BlockHeader;
import com.esiran.greenadmin.chain.entity.HeadIndex;
import com.esiran.greenadmin.chain.mapper.HeadIndexMapper;
import com.esiran.greenadmin.chain.service.IHeadIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
@Service
public class HeadIndexServiceImpl extends ServiceImpl<HeadIndexMapper, HeadIndex> implements IHeadIndexService {

    @Override
    public void setHead(BlockHeader header) {
        Wrapper<HeadIndex> queryWrapper = new LambdaQueryWrapper<HeadIndex>()
                .eq(HeadIndex::getId, 1);
        HeadIndex old = getOne(queryWrapper);
        if (old == null){
            HeadIndex index = new HeadIndex();
            index.setId(1);
            index.setHead(header.getHash());
            save(index);
        }else {
            old.setHead(header.getHash());
            updateById(old);
        }
    }
}
