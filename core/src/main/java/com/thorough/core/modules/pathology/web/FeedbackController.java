package com.thorough.core.modules.pathology.web;


import com.thorough.core.modules.pathology.model.entity.Feedback;
import com.thorough.core.modules.pathology.service.FeedbackServic;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "${adminPath}/pathology/feedback/")
public class FeedbackController extends BaseController {

    @Autowired
    FeedbackServic feedbackServic;

    @RequestMapping(value = "save")
    public ResponseEntity<?> save(Feedback feedback){
        String id = feedbackServic.save(feedback);
        ResponseBuilder builder = ResponseBuilder.newInstance();
        builder.add("id",id);
        return builder.build();
    }
}
