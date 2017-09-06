package service;

import commons.ServerResponse;
import pojo.User;

public interface IUserService {

	ServerResponse<User> login(String username,String password);
	
	 ServerResponse<String> register(User user);
	 
	 ServerResponse<String> checkValid(String str,String type);
	 
	 ServerResponse selectQuestion(String username);
	 
	 ServerResponse<String> checkAnswer(String username,String question,String answer); 
	 
	 ServerResponse<String> forgetRestPassword(String username,String newPassword,String forgetToken);

	 ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

	 ServerResponse<User> updateInformation(User user);

	 ServerResponse<User> getInformation(Integer userId);

	 /**
	  * 校验是否是管理员
	  * @param user
	  * @return
	  */
	 ServerResponse checkAdmin(User user);




}
