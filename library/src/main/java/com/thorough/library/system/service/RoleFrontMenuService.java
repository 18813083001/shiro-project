package com.thorough.library.system.service;

import com.thorough.library.specification.service.BaseService;
import com.thorough.library.system.model.dao.RoleFrontMenuDao;
import com.thorough.library.system.model.entity.FrontMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleFrontMenuService implements BaseService {

    @Autowired
    RoleFrontMenuDao roleFrontMenuDao;
    public List<FrontMenu> getFrontMenuIdsByRoleIds(List<String> roleIds){
        return roleFrontMenuDao.getFrontMenuIdsByRoleIds(roleIds);
    }
}
