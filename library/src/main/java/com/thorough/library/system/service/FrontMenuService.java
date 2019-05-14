package com.thorough.library.system.service;


import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.service.CommonService;
import com.thorough.library.system.model.dao.FrontMenuDao;
import com.thorough.library.system.model.entity.FrontMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class FrontMenuService extends CommonService<String,FrontMenuDao,FrontMenu> {

    @Autowired
    FrontMenuDao frontMenuDao;

    public List<FrontMenu> getAllFrontMenu(){
        CommonExample example = new CommonExample(FrontMenu.class);
        example.createCriteria().andEqualTo(FrontMenu.getFieldDelFlag(),"0").andEqualTo(FrontMenu.getFiledsShow(),"1");
        example.setOrderByClause("sort asc");
        return this.selectByExample(example);
    }
}
