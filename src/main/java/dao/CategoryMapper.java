package dao;

import java.util.List;

import pojo.Category;

public interface CategoryMapper {
	/**
	 * 根据ID删除一个分类
	 * @param id
	 * @return
	 */
    int deleteByPrimaryKey(Integer id);

    /**
     * 添加一个分类
     * @param record
     * @return
     */
    int insert(Category record);

    /**
     * 动态的添加一个分类
     * @param record
     * @return
     */
    int insertSelective(Category record);

    /**
     * 根据ID查询出一个分类对象的信息
     * @param id
     * @return
     */
    Category selectByPrimaryKey(Integer id);

    /**
     * 动态的修改一个分类的信息
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     * 修改一个分类信息
     * @param record
     * @return
     */
    int updateByPrimaryKey(Category record);
    
    /**
     * 查询出分类和子类的所有信息
     * @param parentId
     * @return
     */
    List<Category> selectChildrenParallelByParentId(Integer parentId);
}