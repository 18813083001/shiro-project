package com.thorough.core.modules.pathology.web;

import com.thorough.core.modules.pathology.model.entity.UserBrush;
import com.thorough.core.modules.pathology.service.UserBrushService;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "${adminPath}/pathology/userBrush/")
public class UserBrushController {

    @Autowired
    UserBrushService userBrushService;

    @RequestMapping(value = "/getUserBrush")
    public ResponseEntity<?> getUserBrush(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        User user = UserUtils.getUser();
        String userId = user.getId();
        UserBrush userBrush = userBrushService.selectByPrimaryKey(userId);
        int brushSize = 6;
        if (userBrush != null)
            brushSize = userBrush.getBrushSize();
        builder.add("brushSize",brushSize);
        return builder.build();
    }

    @RequestMapping(value = "/updateUserBrush")
    public ResponseEntity<?> updateUserBrush(@RequestParam Integer brushSize){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        User user = UserUtils.getUser();
        String userId = user.getId();
        UserBrush userBrush = userBrushService.selectByPrimaryKey(userId);
        if (userBrush != null){
            userBrush.setBrushSize(brushSize);
            userBrushService.updateByPrimaryKeySelective(userBrush);
        }else {
            userBrush = new UserBrush();
            userBrush.setUserId(userId);
            userBrush.setBrushSize(brushSize);
            userBrushService.insert(userBrush);
        }
        builder.add("message","更新成功");
        return builder.build();
    }
}
