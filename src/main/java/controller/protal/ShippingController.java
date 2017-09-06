package controller.protal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import pojo.Shipping;
import pojo.User;
import service.IShippingService;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

	@Autowired
	private IShippingService iShippingService;

	@RequestMapping("add.do")
	public ServerResponse add(HttpSession session, Shipping shipping) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.add(user.getId(), shipping);
	}

	@RequestMapping("delete.do")
	public ServerResponse delete(HttpSession session, Integer shippingId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.delete(user.getId(), shippingId);
	}

	@RequestMapping("update.do")
	public ServerResponse update(HttpSession session, Shipping shipping) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.update(user.getId(), shipping);
	}

	@RequestMapping("select.do")
	public ServerResponse<Shipping> select(HttpSession session, Integer shippingId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		return iShippingService.selectByShippingIdUserId(user.getId(), shippingId);
	}

	public ServerResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
					ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iShippingService.list(pageNum, pageSize, user.getId());

	}

}
