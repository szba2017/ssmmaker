package controller.bakend;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.ha.deploy.FileMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;

import commons.Const;
import commons.ResponseCode;
import commons.ServerResponse;
import pojo.Product;
import pojo.User;
import service.IFileService;
import service.IProductService;
import service.IUserService;
import util.PropertiesUtil;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

	@Autowired
	private IUserService iUserService;

	@Autowired
	private IProductService iProductService;

	@Autowired
	private IFileService iFileService;

	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse saveProduct(HttpSession session, Product product) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录管理员账号");
		}
		if (iUserService.checkAdmin(user).isSuccess()) {
			return iProductService.saveOrupdate(product);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作");
		}
	}

	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse setSalStatus(HttpSession session, Product product) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录管理员账号");
		}
		if (iUserService.checkAdmin(user).isSuccess()) {
			return iProductService.setSalStatus(product.getId(), product.getStatus());
		} else {
			return ServerResponse.createByErrorMessage("无权限操作");
		}
	}

	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session, Integer productId) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录管理员账号");
		}
		if (iUserService.checkAdmin(user).isSuccess()) {
			return iProductService.manageProductDetail(productId);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作");
		}
	}

	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse getList(HttpSession session,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录管理员账号");
		}
		if (iUserService.checkAdmin(user).isSuccess()) {
			return iProductService.getProductList(pageNum, pageSize);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作");
		}
	}

	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse searchList(HttpSession session, String productName, Integer productId,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
			@RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录管理员账号");
		}
		if (iUserService.checkAdmin(user).isSuccess()) {
			return iProductService.serachList(productName, productId, pageNum, pageSize);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作");
		}
	}

	@RequestMapping("upload.do")
	@ResponseBody
	public ServerResponse upload(HttpSession session,
			@RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request)
			throws Exception {
		String path = request.getSession().getServletContext().getRealPath("upload");
		Map fileMap = new HashMap();

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登录管理员账号");
		}
		if (iUserService.checkAdmin(user).isSuccess()) {

			String targetFileName = iFileService.upload(file, path);
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

			fileMap.put("uri", targetFileName);
			fileMap.put("url", url);
			return ServerResponse.createBySuccess(fileMap);
		} else {
			return ServerResponse.createByErrorMessage("无权限操作");
		}
	}
	
	//富文本上传
	@RequestMapping("rithtext_img_upload.do")
	@ResponseBody
	public Map rithtextImgUpload(HttpSession session,
			@RequestParam(value = "upload_file", required = false) MultipartFile file,
			HttpServletRequest request,HttpServletResponse response)
			throws Exception {
		Map resultMap = Maps.newHashMap();
		String path = request.getSession().getServletContext().getRealPath("upload");
		Map fileMap = new HashMap();

		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			resultMap.put("success",false);
			resultMap.put("msg","请登录管理员");
			return resultMap;
		}
		if (iUserService.checkAdmin(user).isSuccess()) {
			String targetFileName = iFileService.upload(file, path);
				if(StringUtils.isBlank(targetFileName)) {
					resultMap.put("success",false);
					resultMap.put("msg","上传失败");
					return resultMap;
				}
			String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			resultMap.put("success",true);
			resultMap.put("msg","上传成功");
			resultMap.put("file_path",url);
			response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
			return resultMap;
		} else {
			resultMap.put("success",false);
			resultMap.put("msg","无权限操作");
			return resultMap;
		}
	}
}
