package dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import commons.ServerResponse;
import commons.Const.cart;
import pojo.Cart;
import vo.CartVo;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId")Integer userId,@Param("productId")Integer productId);

    List<Cart> selectCartByUserId(Integer userId);
    
    int selectCartProductCheckedStatusByUserId(Integer userId);
    
    int deleteByUserIdProductIds(@Param("userId")Integer userId,@Param("productIds")List<String> productIds);
    
    int checkedOrUnCheckedAllProduct(@Param("userId")Integer usreId,@Param("checked")Integer checked);
    
    ServerResponse<CartVo> UncheckedOrUnCheckedAllProduct(@Param("userId")Integer userId,@Param("productId")Integer productId,@Param("checked")Integer checked);
    
    int getCartProductCount(Integer userId);
    
    List<Cart> selectCheckedCartByUserId(Integer userId);
    
    
    
    
    
    
    
}