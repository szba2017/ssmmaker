package service;

import com.github.pagehelper.PageInfo;

import commons.ServerResponse;
import pojo.Product;
import vo.ProductDetailVo;

public interface IProductService {

	/**
	 * 添加或修改一个商品详情
	 * @param product
	 * @return
	 */
	 ServerResponse saveOrupdate(Product product);
	 
	 /**
	  * 修改商品的 状态（上架 或 下架）
	  * @param productId
	  * @param status
	  * @return
	  */
	 ServerResponse<String> setSalStatus(Integer productId,Integer status);
	 
	 /**
	  *返回一个vo商品对象
	  * @param productId
	  * @return
	  */
	 ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
	 
	 /**
	  * 分页显示商品
	  * @param pageNum
	  * @param pageSize
	  * @return
	  */
	 ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize);
	 
	 ServerResponse<PageInfo> serachList(String productName,Integer productId,Integer pageNum,Integer pageSize);
	 
	 ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
	 
	 ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer productId,Integer pageNum,Integer pageSize,String orderBy);
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
