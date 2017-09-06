package service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ErrorCoded;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import dao.CategoryMapper;
import dao.ProductMapper;
import pojo.Category;
import pojo.Product;
import service.ICategoryService;
import service.IProductService;
import util.DatetimeUtil;
import util.PropertiesUtil;
import vo.ProductDetailVo;
import vo.ProductListVo;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Autowired
	private ICategoryService iCategoryService;
	
	public ServerResponse saveOrupdate(Product product) {
		if(product!=null) {
			if(StringUtils.isNotBlank(product.getSubImages())) {
				String[] subImageArray = product.getSubImages().split(",");
				if(subImageArray.length>0) {
					product.setMainImage(subImageArray[0]);
				}
			}
			if(product.getId()!=null) {
				int rowCount = productMapper.updateByPrimaryKey(product);
				if(rowCount>0) {
					return ServerResponse.createBySuccess("更新产品成功");
				}
				return ServerResponse.createBySuccess("更新产品失败");
			}else {
				int rowCount = productMapper.insert(product);
				if(rowCount>0) {
					return ServerResponse.createBySuccess("新增产品成功");
				}
				return ServerResponse.createBySuccess("新增产品失败");
				 	
			}
		}
		return ServerResponse.createByErrorMessage("增加或更新产品，参数错误");
	}
	
	public ServerResponse<String> setSalStatus(Integer productId,Integer status){
		if(productId==null || status==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		int rowCount = productMapper.updateByPrimaryKeySelective(product);
		if(rowCount>0) {
			return ServerResponse.createBySuccess("修改状态成功");
		}
		return ServerResponse.createBySuccess("修改状态失败");
	} 
	
	
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
		if(productId == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product==null) {
			return ServerResponse.createByErrorMessage("产品已下架或者已删除");
		}
		ProductDetailVo detailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(detailVo);
	}
	
	private ProductDetailVo assembleProductDetailVo(Product product) {
		ProductDetailVo productDetailVo = new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setSubImages(product.getSubImages());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStatus(product.getStatus());
		productDetailVo.setStock(product.getStock());
		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
		Category category = categoryMapper.selectByPrimaryKey(product.getId());
		if(category==null) {
			productDetailVo.setParentCategoryId(0);
		}else {
			productDetailVo.setParentCategoryId(category.getParentId());
		}
		productDetailVo.setCreateTime(DatetimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DatetimeUtil.dateToStr(product.getUpdateTime()));
		return productDetailVo;
	}

	public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Product> productList = productMapper.selectList();
		List<ProductListVo> productListVos = Lists.newArrayList();
		for (Product product : productList) {
			ProductListVo productListVo = assmbleProductListVo(product);
			productListVos.add(productListVo);
		}
		PageInfo pageResult = new PageInfo(productList);
		pageResult.setList(productListVos);
		return ServerResponse.createBySuccess(pageResult);
	}

	private ProductListVo assmbleProductListVo(Product product) {
		ProductListVo listVo = new ProductListVo();
		listVo.setId(product.getId());
		listVo.setCategoryId(product.getCategoryId());
		listVo.setName(product.getName());
		listVo.setSubtitle(product.getSubtitle());
		listVo.setMainImage(product.getMainImage());
		listVo.setPrice(product.getPrice());
		listVo.setStatus(product.getStatus());
		listVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
		return listVo;
	}

	

	@Override
	public ServerResponse<PageInfo> serachList(String productName,Integer productId,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		if(StringUtils.isNotBlank(productName)) {
			productName = new StringBuilder().append("%").append(productName).append("%").toString();
		}
		List<Product> productList = productMapper.selectListByProductNameAndProductId(productName, productId);
		List<ProductListVo> productListVos = Lists.newArrayList();
		for (Product product : productList) {
			ProductListVo productListVo = assmbleProductListVo(product);
			productListVos.add(productListVo);
		}
		PageInfo pageResult = new PageInfo(productList);
		pageResult.setList(productListVos);
		return ServerResponse.createBySuccess(pageResult);
	}
	
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
		if(productId == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product==null) {
			return ServerResponse.createByErrorMessage("产品已下架或者已删除");
		}
		if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
			return ServerResponse.createByErrorMessage("产品已下架或者已删除");
		}
		ProductDetailVo detailVo = assembleProductDetailVo(product);
		return ServerResponse.createBySuccess(detailVo);
	}
	
	public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer productId,Integer pageNum,Integer pageSize,String orderBy){
		if(StringUtils.isBlank(keyword) && productId==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<Integer> categoryIdList = new ArrayList<>();
		if(productId != null) {
			Category category = categoryMapper.selectByPrimaryKey(productId);
			if(category==null && StringUtils.isNotBlank(keyword)) {
				PageHelper.startPage(pageNum, pageSize);
				List<ProductListVo> list = Lists.newArrayList();
				PageInfo<ProductListVo> info = new PageInfo<>(list);
				return ServerResponse.createBySuccess(info);
			}
			categoryIdList = iCategoryService.selectCategoyAndChildrenById(category.getId()).getData();
		}
		if(StringUtils.isNotBlank(keyword)) {
			keyword = new  StringBuilder().append("%").append(keyword).append("%").toString();			
		}
		PageHelper.startPage(pageNum, pageSize);
		if(StringUtils.isNotBlank(orderBy)) {
			if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
				String[] orderByArray = orderBy.split("_");
				PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
			}
		}
		List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword, categoryIdList.size()==0?null:categoryIdList);
		List<ProductListVo> productListVos = Lists.newArrayList();
		for (Product product : productList) {
			ProductListVo listVo = assmbleProductListVo(product);
			productListVos.add(listVo);
		}
		PageInfo info = new PageInfo(productList);
		info.setList(productListVos);
		return  ServerResponse.createBySuccess(info);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	}


