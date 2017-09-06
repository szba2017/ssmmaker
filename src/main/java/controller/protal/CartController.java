package controller.protal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import pojo.User;
import service.ICartService;
import vo.CartVo;

@Controller
@RequestMapping("/cart/")
public class CartController {

	@Autowired
	private ICartService iCartService;
	
	@RequestMapping("add.do")
	@ResponseBody
	public ServerResponse<CartVo>  add(HttpSession session,Integer count,Integer productId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.add(user.getId(), count, productId);
	}
	
	@RequestMapping("upate.do")
	@ResponseBody
	public ServerResponse<CartVo>  update(HttpSession session,Integer count,Integer productId) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.update(user.getId(), count, productId);
	}
	
	@RequestMapping("delete_product.do")
	@ResponseBody
	public ServerResponse<CartVo>  deleteProduct(HttpSession session,String productIds) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.deleteProduct(user.getId(),productIds);
	}
	
	
	@RequestMapping("lsit.do")
	@ResponseBody
	public ServerResponse<CartVo>  list(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.selectCartVo(user.getId());
	}
	
	@RequestMapping("un_select_all.do")
	@ResponseBody
	public ServerResponse<CartVo>  unSelectAll(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.selectOrUnSelectAll(user.getId(), Const.cart.UN_CHECK);
	}
	
	
	@RequestMapping("get_cart_product_count.do")
	@ResponseBody
	public ServerResponse<Integer>  getCartProductCount(HttpSession session) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createBySuccess(0);
		}
		return iCartService.getCartProductCount(user.getId());
	}
	
	
	
	
	
	
	
	
	
}
