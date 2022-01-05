package tech.xfs.xfschainexplorer.chain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import tech.xfs.xfschainexplorer.chain.entity.Address;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Militch
 * @since 2021-10-24
 */
public interface IAddressService extends IService<Address> {
    void saveOrUpdateAddress(Address address);
    Address getAddress(String address);
    IPage<Address> getListByPage(Page<Address> pg);

}
