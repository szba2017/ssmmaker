package controller.protal;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import pojo.User;
import service.IOrderService;

@Controller
@RequestMapping("/order/")
public class OrderController {

	@Autowired
	private IOrderService iOrderService;
	
	private static final Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@RequestMapping("create.do")
	public ServerResponse create(HttpSession session,Integer shippingid) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		return null;
	}
	
	
	@RequestMapping("pay.do")
	public ServerResponse pay(HttpSession session,Long orderNo,HttpServletRequest request) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		String path = request.getSession().getServletContext().getRealPath("upload");
		return iOrderService.pay(orderNo,user.getId(), path);
	}
	
	@RequestMapping("alipay_callBcck.do")
	public Object alipayCallBack(HttpServletRequest request) {
		Map<String,String> params = Maps.newHashMap();
		Map requestParams = request.getParameterMap();
		for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();) {
			String name = (String)iter.next();
			String[] values = (String[])requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (0==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
			}
			params.put(name, valueStr);
		}
		log.info("支付宝回调:sign:{},trade_status:{},修改:{}",params.get("sign"),params.get("trade_status"),params.toString());
		//非常重要，判断是不是支付宝发的，并且要避免重复发送
		params.remove("sign_type");
		try {
			boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
			if(!alipayRSACheckedV2) {
				return ServerResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求，我就报网警！！！");
			}
		} catch (AlipayApiException e) {
			log.info("支付宝毁掉异常",e);
		}
		//todo 验证各种数据的正确性
		ServerResponse serverResponse = iOrderService.aliCallback(params);
		if(serverResponse.isSuccess()) {
			return Const.AlipayCallback.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallback.RESPONSE_FAILED;
	}

	@RequestMapping("query_order_pay_status.do")
	public ServerResponse<Boolean> queryOrderPaystatus(HttpSession session,Long orderNo) {
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		ServerResponse serverResponse =  iOrderService.queryOrderPaystatus(user.getId(), orderNo);
		if(serverResponse.isSuccess()) {
			return ServerResponse.createBySuccess(true);
		}
		return  ServerResponse.createBySuccess(false);
	}
	
}
