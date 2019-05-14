package com.thorough.core.modules.sys.controller;

import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.service.OfficeService;
import com.thorough.library.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController{

    @Autowired
    private OfficeService officeService;

    @RequestMapping(value = "hospitalList")
    public ResponseEntity<?> getHospital(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        //医院
        List<Map> hospitalList = officeService.getHospitalIdList();
        builder.add("hospitalList",hospitalList);
        return builder.build();
    }



}
