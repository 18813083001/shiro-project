package com.thorough.core.modules.pathology.service;


import com.thorough.core.modules.pathology.model.dao.SubmitUserUserDao;
import com.thorough.core.modules.pathology.model.entity.SubmitUserUser;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SubmitUserUserService extends CommonService<String,SubmitUserUserDao,SubmitUserUser> {

    @Transactional(readOnly = false)
    public int deleteRelation(List<String> imageIdList, String upperId){
        int num = 0;
        for (String userId:imageIdList){
            CommonExample example = new CommonExample(SubmitUserUser.class);
            example.createCriteria().
                    andEqualTo(SubmitUserUser.getFieldReviewUserId(),upperId).
                    andEqualTo(SubmitUserUser.getFieldSubmitUserId(),userId);
            long row = this.deleteByExample(example);
            num += row;
        }
        return num;
    }

    @Transactional(readOnly = false)
    public int addRelation(List<String> imageIdList,String upperId){
        int num = 0;
        for (String userId:imageIdList){
            SubmitUserUser submitUserUser = new SubmitUserUser();
            submitUserUser.setReviewUserId(upperId);
            submitUserUser.setSubmitUserId(userId);
            int row = this.insert(submitUserUser);
            num += row;
        }
        return num;

    }
}
