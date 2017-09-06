package service;

import java.util.List;
import java.util.Map;

import commons.ServerResponse;
import pojo.Cart;
import pojo.OrderItem;

public interface IOrderService {

	ServerResponse pay(Long orderNo,Integer userId,String path);
	
	 ServerResponse aliCallback(Map<String,String> params) ;
	 
	 ServerResponse queryOrderPaystatus(Integer userId,Long orderNo);
	 
	
}
