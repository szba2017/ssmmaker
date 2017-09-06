package service.impl;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import commons.Const;
import commons.ServerResponse;
import commons.TokenCache;
import dao.UserMapper;
import pojo.User;
import service.IUserService;
import util.MD5Util;

@Service("iUserService")
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserMapper userMapper;
	
	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount = userMapper.checkUsername(username);
		if(resultCount==0) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		//todo 密码MD5
		String md5Password = MD5Util.MD5EncodeUtf8(password);
		User user = userMapper.selectLogin(username, md5Password);
		if(user == null) {
			return ServerResponse.createByErrorMessage("密码错误");
		}
		user.setPassword(StringUtils.EMPTY);	
		return ServerResponse.createBySuccess("登录成功", user);
	}
	
	@Override
	public ServerResponse<String> register(User user){
	
		ServerResponse checkValid = this.checkValid(user.getUsername(),Const.USERNAME);
		if(!checkValid.isSuccess()) {
			return checkValid;
		}
		
		checkValid = this.checkValid(user.getEmail(), Const.EMAIL);
		if(!checkValid.isSuccess()) {
			return checkValid;
		}
		
		user.setRole(Const.Role.ROLE_CUSTOMER);
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		int resultCount = userMapper.insert(user);
		if(resultCount==0) {
			return ServerResponse.createByErrorMessage("添加失败");
		}
		return ServerResponse.createBySuccess("添加成功");
	}

	public ServerResponse<String> checkValid(String str,String type){
		if(StringUtils.isNotBlank(type)) {
			if(Const.USERNAME.equals(type)) {
				int resultCount = userMapper.checkUsername(str);
				if(resultCount>0) {
					return ServerResponse.createByErrorMessage("用户名已存在");
				}
			}
			if(Const.EMAIL.equals(type)) {
				int resultCount = userMapper.checkEmail(str);
				if(resultCount>0) {
					return ServerResponse.createByErrorMessage("email已存在");
				}
			}
			
		}else {
			return ServerResponse.createByErrorMessage("参数错误");
		}
		return ServerResponse.createBySuccess("校验成功");
	}

	public ServerResponse selectQuestion(String username) {
		ServerResponse checkValid = this.checkValid(username,Const.USERNAME);
		if(checkValid.isSuccess()) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		String question = userMapper.selectQuestionByUsername(username);
		if(StringUtils.isNotBlank(question)) {
			return ServerResponse.createBySuccess(question);
		}
		return ServerResponse.createByErrorMessage("找回密码的问题是空的");
	}

	public ServerResponse<String> checkAnswer(String username,String question,String answer){
		int resultCount = userMapper.checkAnswer(username, question, answer);
		if(resultCount>0) {
			String forgetToken = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
			return ServerResponse.createBySuccess(forgetToken);
		}
		return ServerResponse.createByErrorMessage("你的答案输入错误");
	}
	
	public ServerResponse<String> forgetRestPassword(String username,String newPassword,String forgetToken){
		if(StringUtils.isBlank(forgetToken)) {
			return ServerResponse.createByErrorMessage("Parameter error,token needs to be passed over");
		}
		ServerResponse checkValid = this.checkValid(username,Const.USERNAME);
		if(checkValid.isSuccess()) {
			return ServerResponse.createByErrorMessage("用户名不存在");
		}
		String token =TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
		if(StringUtils.isBlank(token)) {
			return ServerResponse.createByErrorMessage("Parameter name is invalid or Parameter is invalid");
		}
		if(StringUtils.equals(forgetToken, token)) {
			String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
			int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
			if(resultCount>0) {
				return ServerResponse.createBySuccess("password sucessfully modified");
			}
		}else {
			return ServerResponse.createByErrorMessage("Token error. Please get the token again to change the password");
		}
		return ServerResponse.createByErrorMessage("failed to modify password ");	
	}
	
	public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
		int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
		if(resultCount == 0) {
			return ServerResponse.createByErrorMessage("Old password error");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount = userMapper.updateByPrimaryKeySelective(user);
		if(updateCount>0) {
			return ServerResponse.createBySuccess("password update sucessfully");
		}
		return ServerResponse.createByErrorMessage("password update failed");
	}
	
	public ServerResponse<User> updateInformation(User user){
		int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
		if(resultCount>0) {
			return ServerResponse.createByErrorMessage("email already exists");
		}
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setEmail(user.getEmail());
		updateUser.setPhone(user.getPhone());
		updateUser.setQuestion(user.getQuestion());
		updateUser.setAnswer(user.getAnswer());
		
		int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
		if(updateCount>0) {
			return ServerResponse.createBySuccess("update sucessfully",updateUser);
		}
		return ServerResponse.createByErrorMessage("update failed");
	}
	
	public ServerResponse<User> getInformation(Integer userId){
		User user = userMapper.selectByPrimaryKey(userId);
		if(user==null) {
			return ServerResponse.createByErrorMessage("The current user cannot be found");
		}
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.createBySuccess(user);
	}
	
	/**
	 * 校验是否是管理员
	 * @param user
	 * @return
	 */
	public ServerResponse checkAdmin(User user) {
		if(user !=null && user.getRole().intValue()==Const.Role.ROLE_ADMIN) {
			return  ServerResponse.createBySuccess();
		}
		return ServerResponse.createByError();
	}
}
