package service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import commons.ServerResponse;
import dao.CategoryMapper;
import pojo.Category;
import service.ICategoryService;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

	@Autowired
	private CategoryMapper categoryMapper;
	
	private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	public ServerResponse addCategory(String categroyName,Integer parentId) {
		if(parentId==null || StringUtils.isBlank(categroyName)) {
			return ServerResponse.createByErrorMessage("添加品类参数错误");
		}
		Category category = new Category();
		category.setParentId(parentId);
		category.setName(categroyName);
		category.setStatus(true);//表示此分类可用
		int rowCount = categoryMapper.insert(category);
		if(rowCount>0) {
			return ServerResponse.createBySuccess("添加品类成功");
		}
		return ServerResponse.createByErrorMessage("添加品类失败");
	}
	
	public ServerResponse updateCategoryName(Integer parentId,String categoryName) {
		if(parentId==null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.createByErrorMessage("添加品类参数错误");
		}
		Category category = new Category();
		category.setParentId(parentId);
		category.setName(categoryName);
		int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
		if(rowCount>0) {
			return ServerResponse.createBySuccess("update sucessfully");
		}
		return ServerResponse.createByErrorMessage("update failed");
	}
	
	
	public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId){
		List<Category> categories = categoryMapper.selectChildrenParallelByParentId(parentId);
		if(CollectionUtils.isEmpty(categories)) {
			logger.info("未找到当前分类的子分类");
		}
		return ServerResponse.createBySuccess(categories);
	}
	
	/**
	 * 递归查询本节点的ID及孩子的ID
	 * @param categoryId
	 * @return
	 */
	public ServerResponse<List<Integer>> selectCategoyAndChildrenById(Integer categoryId) {
		Set<Category> categorySet = Sets.newHashSet();
		findChildrenCategory(categorySet, categoryId);
		List<Integer> categoryIdList=Lists.newArrayList();
		if(categoryId != null) {
			for (Category categoryItem : categorySet) {
				categoryIdList.add(categoryItem.getId());
			}
		}
		return  ServerResponse.createBySuccess(categoryIdList);
	}
	
	//递归算法，算出子节点
	public Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
		Category category = categoryMapper.selectByPrimaryKey(categoryId);
		if(category!=null) {
			categorySet.add(category);
		}
		//查找子节点，递归算法一定要有个结束条件
		List<Category> categoryList = categoryMapper.selectChildrenParallelByParentId(categoryId);
		for (Category categoryItem : categoryList) {
			findChildrenCategory(categorySet, categoryItem.getId());
		}
		return categorySet;
	}
}
