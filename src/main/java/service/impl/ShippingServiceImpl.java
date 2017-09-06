package service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import dao.ShippingMapper;
import pojo.Shipping;
import pojo.User;
import service.IShippingService;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{

	@Autowired
	private ShippingMapper shippingMapper;
	
	public ServerResponse add(Integer userId,Shipping shipping) {
		shipping.setId(userId);
		int rowCount = shippingMapper.insert(shipping);
		if(rowCount>0) {
			Map result = Maps.newHashMap();
			result.put("shippingId",shipping.getId());
			return ServerResponse.createBySuccess("新建地址成功",result);
		}else {
			return ServerResponse.createByErrorMessage("新建地址失败");
		}
	}
	
	public ServerResponse<String> delete(Integer userId,Integer shippingId){
		int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
		if(resultCount>0) {		
			return ServerResponse.createBySuccess("新建地址成功");
		}else {
			return ServerResponse.createByErrorMessage("新建地址失败");
		}	
	}
	
	public ServerResponse update(Integer userId,Shipping shipping){
		shipping.setId(userId);
		int resultCount = shippingMapper.updateShipping(shipping);
		if(resultCount>0) {		
			return ServerResponse.createBySuccess("修改地址成功");
		}else {
			return ServerResponse.createByErrorMessage("修改地址失败");
		}	
	}
	
	public ServerResponse<Shipping> selectByShippingIdUserId(Integer userId,Integer shippingId){
		Shipping shipping  = shippingMapper.selectByShippingIdUserId(userId, shippingId);
		if(shipping == null) {
			return ServerResponse.createByErrorMessage("对不起，没有相关记录");
		}
		return ServerResponse.createBySuccess("查询成功",shipping); 
	}
	
	public ServerResponse<PageInfo> list(Integer pageNum,Integer pageSize,Integer userId){
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
		PageInfo pageInfo = new PageInfo<>(shippingList);
		return ServerResponse.createBySuccess(pageInfo);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
