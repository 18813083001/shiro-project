package com.thorough.library.system.model.dao;


import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface UserUserDao extends Dao {
    List<String> getUpperIdList(@Param(value = "userId") String userId);
    List<String> getBelongsIdList(@Param(value = "upperId") String upperId);
    List<String> getAllUpperIdList();
    int deleteRelation(@Param(value = "userId") String userId, @Param(value = "upperId") String upperId);
    int addRelation(@Param(value = "userId") String userId, @Param(value = "upperId") String upperId, @Param(value = "createBy") String createBy);
}
