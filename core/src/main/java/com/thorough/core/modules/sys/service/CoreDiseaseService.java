package com.thorough.core.modules.sys.service;

import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.service.AiModelService;
import com.thorough.core.modules.pathology.service.ImageUserService;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.service.DiseaseService;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = false)
public class CoreDiseaseService extends DiseaseService{

    @Autowired
    ImageUserService imageUserService;
    @Autowired
    AiModelService aiModelService;
    @Autowired
    CoreDiseaseService coreDiseaseService;

    @Override
    public long statisticsImageCountByDiseaseId(String diseaseId, int aiPredict) {
          //暂时不用
//        List<String> diseaseIdList ;
//        User user = UserUtils.getUser();
//        Image image = new Image();
//        //统计非疑难的切片
//        image.setPrivateDifficult(0);
//        //获取某疾病及其子疾病下的切片(按角色->配置项数据)
//        diseaseIdList = this.getAllChildIdsByParentIdAndUserId(diseaseId,user.getId(),"disease");
//        //防止查询所有配置项
//        if (diseaseIdList == null)
//            diseaseIdList = new ArrayList<>();
//        image.setUserId(user.getId());
//        image.setDiseaseIdList(diseaseIdList);
//        long count = imageUserService.getCountByUser(image);
        return 0;
    }

    @Override
    public boolean haveModel(Disease data) {
        //暂时不用
//        aiModelService.haveModel(data);
        return false;
    }

    public List<Map> getUserOrgan(){
        List<String> userIdList;
        List<Map> usersOrganList;
        if (UserUtils.getUser().isAdmin()){
            usersOrganList = coreDiseaseService.getDirectOrganListByParentId("0",1);
        }else {
            userIdList = UserUtils.getBelongUserIds();
            usersOrganList = coreDiseaseService.getDirectAvailableDiseaseListByUserIdListAndParentId(userIdList,"0",1);
        }
        return usersOrganList;
    }
}
