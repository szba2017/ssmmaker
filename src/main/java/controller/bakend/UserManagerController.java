package controller.bakend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import commons.Const;
import commons.ServerResponse;
import pojo.User;
import service.IUserService;

@Controller
@RequestMapping("/manager/user")
public class UserManagerController {

	@Autowired
	private IUserService iUserService;
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username,String password,HttpSession session){
		ServerResponse<User> response = iUserService.login(username, password);
		if(response.isSuccess()) {
			User user = response.getData();
			if(user.getRole()==Const.Role.ROLE_ADMIN) {
				session.setAttribute(Const.CURRENT_USER, user);
				return response;
			}else {
				return ServerResponse.createByErrorMessage("Not an administrator,unable to login");
			}
		}
		return response;
	}
}
