package com.thorough.library.specification.controller;

import com.thorough.library.utils.ResponseBuilder;
import com.thorough.library.utils.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController{

    @RequestMapping("/error")
    public ResponseEntity<?> handleError(HttpServletRequest request, HttpServletResponse response){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        builder.setWrapperCode(response.getStatus()+"");
        builder.error();
        return builder.build();
    }
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
