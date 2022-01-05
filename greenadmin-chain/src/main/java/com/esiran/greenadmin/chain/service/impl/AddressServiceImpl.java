package com.esiran.greenadmin.chain.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esiran.greenadmin.chain.entity.Address;
import com.esiran.greenadmin.chain.mapper.AddressMapper;
import com.esiran.greenadmin.chain.service.IAddressService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    private static final ModelMapper mp = new ModelMapper();
    static {
        mp.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
    }
    @Override
    public void saveOrUpdateAddress(Address address) {
        address.setId(null);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        Address old = getAddress(address.getAddress());
        if (old != null){
            address.setCreateTime(null);
            mp.map(address, old);
            address = old;
        }
        saveOrUpdate(address);
    }

    @Override
    public Address getAddress(String address) {
        Wrapper<Address> wrapper = new LambdaQueryWrapper<Address>()
                .eq(Address::getAddress, address).eq(Address::getDisplay,true);
        return getOne(wrapper);
    }

    @Override
    public IPage<Address> getListByPage(Page<Address> pg) {
        Wrapper<Address> wrapper = new LambdaQueryWrapper<Address>()
                .eq(Address::getDisplay, true)
                .orderByDesc(Address::getCreateTime);
        return page(pg, wrapper);
    }
}
