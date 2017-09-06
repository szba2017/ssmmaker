package vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {

	private List<CartProductVo> cartProductVos;
	private BigDecimal cartTotalPrice;
	private boolean allChecked;//是否已经都勾选
	private String imgeHost;
	public List<CartProductVo> getCartProductVos() {
		return cartProductVos;
	}
	public void setCartProductVos(List<CartProductVo> cartProductVos) {
		this.cartProductVos = cartProductVos;
	}
	public BigDecimal getCartTotalPrice() {
		return cartTotalPrice;
	}
	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}
	public boolean isAllChecked() {
		return allChecked;
	}
	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}
	public String getImgeHost() {
		return imgeHost;
	}
	public void setImgeHost(String imgeHost) {
		this.imgeHost = imgeHost;
	}
	
	
}
