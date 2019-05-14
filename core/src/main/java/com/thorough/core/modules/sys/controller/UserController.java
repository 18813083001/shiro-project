
package com.thorough.core.modules.sys.controller;

import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.service.SystemService;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.ResponseBuilder;
import com.thorough.library.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemService systemService;
	
	@ModelAttribute
	public User get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getUser(id);
		}else{
			return new User();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = {"listData"})
	public Page<User> listData(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		return page;
	}


	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 返回用户信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "infoData")
	public User infoData() {
		return UserUtils.getUser();
	}

	/**
	 * 修改个人用户密码
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@RequestMapping(value = "modifypwd")
	public ResponseEntity<?> modifypwd(String oldPassword, String newPassword) {
		ResponseBuilder builder = ResponseBuilder.newInstance();
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if (SystemService.validatePassword(oldPassword, user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				builder.message("修改密码成功");
			}else{
				builder.error();
				builder.message("修改密码失败，旧密码错误");
			}
		}else {
			builder.error();
			builder.message("旧密码或者新密码为空");
		}
		return builder.build();
	}

	/**
	 * 修改个人用户密码
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "modifName")
	public ResponseEntity<?> modifName(String name) {
		ResponseBuilder builder = ResponseBuilder.newInstance();
		if (StringUtils.isNotBlank(name)){
			User user = UserUtils.getUser();
			user.setOldLoginName(user.getLoginName());
			user.setLoginName(name);
			systemService.updateUserInfo(user);
			builder.message("修改登录名成功！");
			builder.add("loginName",name);
		}else{
			builder.error();
			builder.message("修改登录名失败，名字为空");
		}

		return builder.build();
	}

	@RequestMapping(value = "userList")
	public ResponseEntity<?> belongUserNameList(){
		ResponseBuilder builder = ResponseBuilder.newInstance();
		//用户
		List<Map<String,String>> belongsUserNameList = UserUtils.getBelongsUserNameList();
		builder.add("userList",belongsUserNameList);
		return builder.build();
	}

	@RequestMapping(value = "userListByHospitalId")
	public ResponseEntity<?> getUserListByHospital(String hospitalId){
		ResponseBuilder builder = ResponseBuilder.newInstance();
		List<Map<String,String>> userListByHospital = UserUtils.getUserIdNameTypeByHospitalId(hospitalId,null);
		builder.add("userList",userListByHospital);
		return builder.build();
	}

	@RequestMapping(value = "expertListByHospitalId")
	public ResponseEntity<?> getExpertListByHospital(String hospitalId){
		ResponseBuilder builder = ResponseBuilder.newInstance();
		List<Map<String,String>> expertListByHospital = UserUtils.getUserIdNameTypeByHospitalId(hospitalId, Constant.USER_EXPERT);
		List<Map<String,String>> advisorListByHospital = UserUtils.getUserIdNameTypeByHospitalId(hospitalId,Constant.USER_ADVISOR);
		List<Map<String,String>> list = new ArrayList<>();
		if (expertListByHospital != null)
			list.addAll(expertListByHospital);
		if (advisorListByHospital != null)
			list.addAll(advisorListByHospital);
		builder.add("userList",list);
		return builder.build();
	}






}
