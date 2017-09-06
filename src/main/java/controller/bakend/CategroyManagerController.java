package controller.bakend;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import pojo.User;
import service.ICategoryService;
import service.IUserService;

@Controller
@RequestMapping("/manage/catogry")
public class CategroyManagerController {

	@Autowired
	private IUserService iUserService;
	
	@Autowired
	private ICategoryService iCategoryService;

	@RequestMapping("add_category.do")
	@ResponseBody
	public ServerResponse addCategory(HttpSession session, String categoryName,
			@RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "not an login");
		}
		if(iUserService.checkAdmin(user).isSuccess()) {
			return iCategoryService.addCategory(categoryName, parentId);
		}else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}

	@RequestMapping("set_category_name.do")
	@ResponseBody
	public  ServerResponse setCategoryName(HttpSession session,Integer parentId,String categoryName) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "not an login");
		}
		if(iUserService.checkAdmin(user).isSuccess()) {
			return iCategoryService.updateCategoryName(parentId, categoryName);
		}else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}
	
	@RequestMapping("get_children_paraller_category.do")
	@ResponseBody
	public ServerResponse getChildrenParallerCategory(HttpSession session,@RequestParam(value="parentId",defaultValue="0")Integer parentId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "not an login");
		}
		if(iUserService.checkAdmin(user).isSuccess()) {
			return iCategoryService.getChildrenParallelCategory(parentId);
		}else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}
	
	@RequestMapping("get_deep_category.do")
	@ResponseBody
	public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,
			@RequestParam(value="parentId",defaultValue="0")Integer categoryId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "not an login");
		}
		if(iUserService.checkAdmin(user).isSuccess()) {
			return iCategoryService.selectCategoyAndChildrenById(categoryId);
		}else {
			return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
		}
	}
}
