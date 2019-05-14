package com.thorough.core.modules.pathology.web;

import com.thorough.core.modules.pathology.model.entity.ImageUserComment;
import com.thorough.core.modules.pathology.service.ImageUserCommentService;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "${adminPath}/pathology/comment/")
public class ImageUserCommentController {

    @Autowired
    ImageUserCommentService imageUserCommentService;

    @RequestMapping(value = "/add")
    public ResponseEntity<?> add(@RequestParam String imageId, @RequestParam Integer grade, String comment) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        return builder.build();
    }

    @RequestMapping(value = "/get")
    public ResponseEntity<?> add(@RequestParam String imageId, HttpServletRequest request, HttpServletResponse response) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        CommonExample example = new CommonExample(ImageUserComment.class);
        example.createCriteria().andEqualTo(ImageUserComment.getFieldImageId(),imageId);
        Page page = new Page<>(request,response);
        example.setPage(page);
        List list = imageUserCommentService.selectByExample(example);
        if (list != null) {
            builder.add("list", list);
            builder.add("total", page.getCount());
        } else {
            builder.add("list", new ArrayList<>());
            builder.add("total", 0);
        }
        return builder.build();
    }
}
