package service;

import javax.servlet.http.HttpSession;

import com.github.pagehelper.PageInfo;

import commons.ServerResponse;
import pojo.Shipping;

public interface IShippingService {

	ServerResponse add(Integer userId,Shipping shipping);

	ServerResponse<String> delete(Integer userId,Integer shippingId);

	ServerResponse update(Integer userId,Shipping shipping);
	
	ServerResponse<Shipping> selectByShippingIdUserId(Integer userId,Integer shippingId);

	ServerResponse<PageInfo> list(Integer pageNum,Integer pageSize,Integer userId);

}



