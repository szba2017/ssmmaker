package service;

import java.util.List;

import commons.ServerResponse;
import pojo.Category;

public interface ICategoryService {

	ServerResponse addCategory(String categroyName,Integer parentId);
	
	ServerResponse updateCategoryName(Integer parentId,String categoryName);
	
	ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);
	
	ServerResponse<List<Integer>> selectCategoyAndChildrenById(Integer categoryId);
}
