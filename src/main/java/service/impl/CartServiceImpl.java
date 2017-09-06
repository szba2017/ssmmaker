package service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.catalina.User;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import dao.CartMapper;
import dao.ProductMapper;
import pojo.Cart;
import pojo.Product;
import service.ICartService;
import util.BigDecimalUtil;
import util.PropertiesUtil;
import vo.CartProductVo;
import vo.CartVo;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

	@Autowired
	private CartMapper cartMapper;
	
	@Autowired
	private ProductMapper productMapper;
	
	public ServerResponse<CartVo> add(Integer userId,Integer count,Integer productId) {
		if(count==null || productId==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if(cart == null) {
			 Cart cartItem = new Cart();
			 cartItem.setUserId(userId);
			 cartItem.setQuantity(count);
			 cartItem.setChecked(Const.cart.CHECK);
			 cartItem.setProductId(productId);
			 cartMapper.insert(cartItem);
		}else {
			count = count +cart.getQuantity();
			cart.setQuantity(count);
			cartMapper.updateByPrimaryKeySelective(cart);
		}
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	
	public  ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
		List<String> productList = Splitter.on(",").splitToList(productIds);
		if(CollectionUtils.isEmpty(productList)) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		cartMapper.deleteByUserIdProductIds(userId, productList);
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	
	public ServerResponse<CartVo> selectCartVo(Integer userId) {
		return list(userId);
	}
	
	public ServerResponse<CartVo> list(Integer userId){
		CartVo cartVo = this.getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	
	public ServerResponse<CartVo> selectOrUnSelectAll(Integer userId,Integer checked){
		cartMapper.checkedOrUnCheckedAllProduct(userId, checked);
		return this.list(userId);
	}
	
	public ServerResponse<CartVo> UnSelectOrUnSelectAll(Integer userId,Integer productId,Integer checked){
		cartMapper.UncheckedOrUnCheckedAllProduct(userId,productId,checked);
		return this.list(userId);
	}
	
	private CartVo getCartVoLimit(Integer userId) {
		CartVo cartVo = new CartVo();
		List<Cart> cartList = cartMapper.selectCartByUserId(userId);
		List<CartProductVo> cartProductVoList = Lists.newArrayList();
		BigDecimal cartTotalPrice = new BigDecimal("0");
		
		if(CollectionUtils.isNotEmpty(cartList)) {
			for (Cart cartItem : cartList) {
				CartProductVo cartProductVo = new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(cartItem.getUserId());
				cartProductVo.setProductId(cartItem.getProductId());
				Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
				if(product != null) {
					cartProductVo.setProductMainImage(product.getMainImage());
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());
					//判断库存
					int buyLimitCount = 0;
					if(product.getStock()>=cartItem.getQuantity()) {
						cartProductVo.setLimitQuantity(Const.cart.LIMIT_NUM_SUCCESS);;
						buyLimitCount = cartItem.getQuantity();
					}else {
						buyLimitCount = product.getStock();
						cartProductVo.setLimitQuantity(Const.cart.LIMIT_NUM_FAIL);
						Cart cartForquantity = new Cart();
						cartForquantity.setId(cartItem.getId());
						cartForquantity.setQuantity(buyLimitCount);
						cartMapper.updateByPrimaryKeySelective(cartForquantity);
					}
					cartProductVo.setQuantity(buyLimitCount);
					//计算购物车中某件商品的总价格
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
					cartProductVo.setProductChecked(cartItem.getChecked());
				}
				if(cartItem.getChecked()==Const.cart.CHECK) {
					//如果已经被勾选，则增加到总价中
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
				}
				cartProductVoList.add(cartProductVo);
			}
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVos(cartProductVoList);
		cartVo.setAllChecked(this.getAllCheckedStatus(userId));
		cartVo.setImgeHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVo;
	}
	
	public ServerResponse<CartVo> update(Integer userId,Integer count,Integer productId){
		if(count==null || productId==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
		if(cart != null) {
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKeySelective(cart);
		CartVo cartVo = getCartVoLimit(userId);
		return ServerResponse.createBySuccess(cartVo);
	}
	
	
	private boolean getAllCheckedStatus(Integer userId) {
		if(userId==null) {
			return false;
		}
		return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
	}

	
	public ServerResponse<Integer>  getCartProductCount(Integer userId){
		if(userId==null) {
			return ServerResponse.createBySuccess(0);
		}
		return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
	}
	
	
	
	
	
}
