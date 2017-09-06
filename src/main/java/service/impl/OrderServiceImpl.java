package service.impl;

import java.io.Console;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import commons.Const;
import commons.ServerResponse;
import commons.Const.cart;
import dao.CartMapper;
import dao.OrderItemMapper;
import dao.OrderMapper;
import dao.PayInfoMapper;
import dao.ProductMapper;
import pojo.Cart;
import pojo.Order;
import pojo.OrderItem;
import pojo.PayInfo;
import pojo.Product;
import service.IOrderService;
import util.BigDecimalUtil;
import util.DatetimeUtil;
import util.FtpUtil;
import util.PropertiesUtil;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService{

	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	@Autowired
	private PayInfoMapper payInfoMapper;
	
	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	public ServerResponse pay(Long orderNo,Integer userId,String path) {
		Map<String, String> resultMap = Maps.newHashMap();
		Order order = orderMapper.selectByUserIdAndOrderId(userId, orderNo);
		if(order == null) {
			return ServerResponse.createByErrorMessage("sorry，this user does not have this order");
		}
		resultMap.put("orderNo", String.valueOf(order.getOrderNo()));
		

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happy mmall 扫码支付，订单号").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共：").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
     // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        for (OrderItem orderItem : orderItemList) {
        	 GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName() ,
        			 BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(), orderItem.getQuantity());
        	 goodsDetailList.add(goods1);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
            .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            //授权回调地址
            .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);
        
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if(!folder.exists()) {
                	folder.setWritable(true);
                	folder.mkdirs();
                }
                
                // 需要修改为运行机器上的路径
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("/qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path,qrFileName);
                log.info("qrPath:" + qrPath);
			try {
				FtpUtil.uploadFile(Lists.newArrayList(targetFile));
			} catch (Exception e) {
				log.error("上传的二维码异常",e);
			}
                String qrUr = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrUr", qrUr);
                return ServerResponse.createBySuccess(resultMap);
                
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
	}
	
	  // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                    response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
    
    public ServerResponse aliCallback(Map<String,String> params) 
    {
    	Long orderNo = Long.parseLong(params.get("out_trade_no"));
    	String tradeNo = params.get("trade_no");
    	String tradeStatus = params.get("trade_status");
    	Order order = orderMapper.selecByOrderNo(orderNo);
    	if(order == null) {
    		return ServerResponse.createByErrorMessage("sorry ,this is not my shopping");
    	}
    	if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()) {
    		return ServerResponse.createByErrorMessage("this is again");
    	}
    	if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
    		order.setPaymentTime(DatetimeUtil.strToDate(params.get("gmt_payment")));
    		order.setStatus(Const.OrderStatusEnum.PAID.getCode());
    		orderMapper.updateByPrimaryKeySelective(order);
    	}
    	PayInfo payInfo = new PayInfo();
    	payInfo.setUserId(order.getUserId());
    	payInfo.setOrderNo(order.getOrderNo());
    	payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
    	payInfo.setPlatformNumber(tradeNo);
    	payInfo.setPlatformStatus(tradeStatus);
    	payInfoMapper.insert(payInfo);
    	return ServerResponse.createBySuccess();
}
    
    public ServerResponse queryOrderPaystatus(Integer userId,Long orderNo) {
    	Order order = orderMapper.selecByOrderNo(orderNo);
    	if(order == null) {
    		return ServerResponse.createByErrorMessage("sorry ,this is not my shopping");
    	}
    	if(order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()) {
    		return ServerResponse.createByErrorMessage("this is again");
    	}
    	return ServerResponse.createByError();
    	
    }
    
    public ServerResponse<Object> createOrder(Integer userId,Integer shippingId){
    	List<Cart> cartList = cartMapper.selectCartByUserId(userId);
    	ServerResponse serverResponse = getCartOrderItem(userId, cartList);
    	if(!serverResponse.isSuccess()) {
    		return serverResponse;
    	}
    	List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
    	BigDecimal decimal = this.getOrderItmeTotals(orderItemList);
    	Order order = this.assembleOrder(userId, shippingId, decimal);
    	if(order==null) {
    		return serverResponse.createByErrorMessage("生成的订单为空");
    	}
    	if(CollectionUtils.isEmpty(orderItemList)) {
    		return ServerResponse.createByErrorMessage("购物车为空");
    	}
    	for (OrderItem orderItem : orderItemList) {
			orderItem.setOrderNo(order.getOrderNo());
		}
    	//mybatis 批量插入
    	orderItemMapper.batchInsert(orderItemList);
    	//减少产品种的库存
    	this.reduceProductStock(orderItemList);
    	//清空购物车
    	this.cloneCart(cartList);
    	
    	return ServerResponse.createBySuccess("生成订单成功");
    }
    
    private void cloneCart(List<Cart> cartList) {
    	for (Cart cart : cartList) {
			cartMapper.deleteByPrimaryKey(cart.getId());
		}
    }
    
    private void reduceProductStock(List<OrderItem> orderItemList) {
    	for (OrderItem orderItem : orderItemList) {
			Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
			product.setStock(product.getStock()-orderItem.getQuantity());
			productMapper.updateByPrimaryKeySelective(product);
		}
    }
    
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment) {
    	Long orderNo = this.generateOrderNo();
    	Order order = new Order();
    	order.setOrderNo(orderNo);
    	order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
    	order.setPostage(0);
    	order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
    	order.setPayment(payment);
    	order.setUserId(userId);
    	order.setShippingId(shippingId);
    	int rowCount = orderMapper.insert(order);
    	if(rowCount>0) {
    		return order;
    	}
    	return null;
    }
    
    private Long generateOrderNo() {
    	Long currentTime = System.currentTimeMillis();
    	return currentTime+currentTime%10;
    }
    
    
    
    private BigDecimal getOrderItmeTotals(List<OrderItem> orderItemList) {
    	BigDecimal decimal = new BigDecimal("0");
    	for (OrderItem orderItem : orderItemList) {
    		decimal = BigDecimalUtil.add(decimal.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
    	return decimal;
    }
    
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
    	List<OrderItem> orderItemList = Lists.newArrayList();
    	if(CollectionUtils.isEmpty(orderItemList)) {
    		return ServerResponse.createByErrorMessage("shopping car isEmpty");
    	}
    	for (Cart cartItem : cartList) {
			OrderItem orderItem = new OrderItem();
			Product product = productMapper.selectByPrimaryKey(cartItem.getId());
			if(Const.ProductStatusEnum.ON_SALE.getCode()!=product.getStatus()) {
				return ServerResponse.createByErrorMessage("产品"+product.getName()+"不在售卖状态");
			}
			if(cartItem.getQuantity()>product.getStock()) {
				return ServerResponse.createByErrorMessage("库存不足");
			}
			orderItem.setUserId(userId);
			orderItem.setProductId(product.getId());
			orderItem.setProductName(product.getName());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setCurrentUnitPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
			orderItemList.add(orderItem);
    	}
    	return ServerResponse.createBySuccess(orderItemList);
    }
    
    
    
    
    
}