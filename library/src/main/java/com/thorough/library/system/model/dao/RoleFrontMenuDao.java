package com.thorough.library.system.model.dao;


import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import com.thorough.library.system.model.entity.FrontMenu;
import com.thorough.library.system.model.entity.Role;

import java.util.List;

@MyBatisDao
public interface RoleFrontMenuDao extends Dao {

    List<FrontMenu> getFrontMenuIdsByRoleIds(List<String> roleIds);
    public int insertRoleFrontMenu(Role role);
    public int deleteRoleFrontMenu(Role role);

}
