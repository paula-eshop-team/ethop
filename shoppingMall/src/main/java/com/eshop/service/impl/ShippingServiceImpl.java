package com.eshop.service.impl;

import com.eshop.common.ServerResponse;
import com.eshop.dao.ShippingMapper;
import com.eshop.pojo.Shipping;
import com.eshop.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Paula Lin
 *
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

	private static Logger logger = LoggerFactory.getLogger(ShippingServiceImpl.class);
	
    @Autowired
    private ShippingMapper shippingMapper;

    /**
     * @param userId
     * @param shipping
	 * @return
	 * 
	 */
    public ServerResponse addShippingAddress(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insertShipping(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    /**
     * @param userId
     * @param shippingId
	 * @return
	 * 
	 */
    public ServerResponse<String> deleteShippingAddress(Integer userId,Integer shippingId){
        int resultCount = shippingMapper.deleteShipingByShippingIdUserId(userId,shippingId);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * @param userId
     * @param shipping
	 * @return
	 * 
	 */
    public ServerResponse updateShippingAddress(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateShippingByShipping(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    /**
     * @param userId
     * @param shippingId
	 * @return
	 * 
	 */
    public ServerResponse<Shipping> selectShippingAddress(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectShippingByShippingIdUserId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("更新地址成功",shipping);
    }

    /**
     * @param userId
     * @param pageNum
     * @param pageSize
	 * @return
	 * 
	 */
    public ServerResponse<PageInfo> listShippingAddress(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectShippingByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

	@Override
	public List<Shipping> findAllShippingListInfo() {
		return shippingMapper.findAllShippingListInfo();
	}







}
