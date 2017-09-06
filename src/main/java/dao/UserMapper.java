package dao;

import org.apache.ibatis.annotations.Param;

import pojo.User;

public interface UserMapper {
	
	/**
	 * 根据ID删除一个用户信息
	 * @param id
	 * @return
	 */
    int deleteByPrimaryKey(Integer id);

    /**
     * 增加一个用户信息
     * @param record
     * @return
     */
    int insert(User record);

    /**
     * 动态增加一个用户信息
     * @param record
     * @return
     */
    int insertSelective(User record);

    /**
     * 根据ID查询出一个用户信息
     * @param id
     * @return
     */
    User selectByPrimaryKey(Integer id);

    /**
     * 动态更新一个用户信息
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * 更新一个用户信息
     * @param record
     * @return
     */
    int updateByPrimaryKey(User record);
    
    /**
     * 根据姓名查询出一个用户,判断用户名是否被占用
     * @param username
     * @return
     */
    int checkUsername(String username);

    /**
     * 根据email查询，判断此email有没有被占用
     * @param email
     * @return
     */
    int checkEmail(String email);
    
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username")String username,@Param("password")String password);
    
    /**
     * 根据用户名查询出属于这个用户的问题
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);
    
    /**
     * 根据用户名，判断这个用户的问题跟答案是否匹配
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    /**
     * 修改用户的密码
     * @param username
     * @param passwordNew
     * @return
     */
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    /**
     * 根据ID查询出这个用户的密码
     * @param password
     * @param userId
     * @return
     */
    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);

    /**
     * 根据ID查询出这个用户，并判断此email是否存在
     * @param email
     * @param userId
     * @return
     */
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);


}