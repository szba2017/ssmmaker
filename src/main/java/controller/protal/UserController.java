package controller.protal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import pojo.User;
import service.IUserService;

@Controller
@RequestMapping(value="/user")
public class UserController {

	@Autowired
	private IUserService  iUserService;
	
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username,String password,HttpSession session) {
			ServerResponse<User> response = iUserService.login(username, password);
			if(response.isSuccess()) {
				session.setAttribute(Const.CURRENT_USER,response.getData());
			}
		return response;
	}
	
	@RequestMapping(value="logout.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccess();
	}
	
	@RequestMapping(value="register.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user){
		return iUserService.register(user);
	}
	
	@RequestMapping(value="check_valid.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){
		return iUserService.checkValid(str, type);
	}
	
	@RequestMapping(value="get_user_info.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		User user = (User)session.getAttribute(Const.CURRENT_USER);
		if(user != null) {
			return ServerResponse.createBySuccess(user);
		}
		return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
	}
	
	@RequestMapping(value="forget_get_question.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
		return iUserService.selectQuestion(username);
	}
	
	@RequestMapping(value="forget_check_answer.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		return iUserService.checkAnswer(username, question, answer);
	}

	@RequestMapping(value="forget_rest_password.do",method=RequestMethod.POST)
	@ResponseBody	
	public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
		return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
	}

	@RequestMapping(value="reset_password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if(user == null) {
			return ServerResponse.createByErrorMessage("user not logged in");
		}
		return iUserService.resetPassword(passwordOld, passwordNew, user);
	}
	
	@RequestMapping(value="update_information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> update_information(HttpSession session,User user){
		User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
		if(currentUser == null) {
			return ServerResponse.createByErrorMessage("user not logged in");
		}
		user.setId(currentUser.getId());
		user.setUsername(currentUser.getUsername());
		ServerResponse<User> updateInformation = iUserService.updateInformation(user);
		if(updateInformation.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER, updateInformation.getData());
		}
		return updateInformation;
	}
	
	@RequestMapping(value="get_information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> get_information(HttpSession session){
		User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
		if(currentUser==null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "Not logged in,Mandatory logon is required,status=10");
		}
		return iUserService.getInformation(currentUser.getId());
	}
	
	
	
	
	
	
	
	
	
}
