package service;

import commons.ServerResponse;
import vo.CartVo;

public interface ICartService {

	ServerResponse<CartVo> add(Integer userId,Integer count,Integer productId); 
	
	ServerResponse<CartVo> update(Integer userId,Integer count,Integer productId);
	
	ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);
	
	ServerResponse<CartVo> selectCartVo(Integer userId);
	
	ServerResponse<CartVo> list(Integer userId);
	
	ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked);

	ServerResponse<CartVo> UnSelectOrUnSelectAll(Integer userId,Integer productId,Integer checked);

	ServerResponse<Integer>  getCartProductCount(Integer userId);


}
