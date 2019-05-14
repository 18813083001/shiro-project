package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.FeedbackDao;
import com.thorough.core.modules.pathology.model.entity.Feedback;
import com.thorough.library.specification.service.CommonService;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeedbackServic extends CommonService<String,FeedbackDao,Feedback> {

    @Transactional(readOnly = false)
    public String save(Feedback feedback){
        feedback.preInsert();
        User user = UserUtils.getUser();
        feedback.setUserName(user.getLoginName());
        this.insert(feedback);
        return String.valueOf(feedback.getId());
    }

}
