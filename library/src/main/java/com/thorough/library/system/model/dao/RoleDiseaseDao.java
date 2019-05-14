package com.thorough.library.system.model.dao;

import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.system.model.vo.ParentDiseaseInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface RoleDiseaseDao extends Dao {
    List<String> getDiseaseChildIdsByParentId(@Param(value = "parentId") String parentId);
    List<Disease> getDirectChildsByParentIdAndRoleId(Map param);
    List<Disease> getChildsFromDiseaseIdListAndCategoryAndRole(@Param(value = "diseaseIds") List diseaseIds, @Param(value = "category") String category, @Param(value = "roleIds") List roleIds);
    List<Disease> getChildsFromDiseaseIdListAndCategory(@Param(value = "diseaseIds") List diseaseIds, @Param(value = "category") String category);
    List<String> getDiseaseIdsByRoleIds(@Param(value = "roleIds") List roleIds);
    List<Disease> getDiseaseByRoleIds(@Param(value = "roleIds") List roleIds);
    int insertRoleDisease(Map param);
    int deleteRoleDisease(@Param(value = "roleId") String roleId);
    /**
     * @param category 可以为空
     * @param parentId 可以为空，parentId可以是器官、染色、分类的id
     * */
    List<String> getDiseaseIdListByUserIdList(@Param(value = "userIds") List<String> userIds,
                                              @Param(value = "category") String category,
                                              @Param(value = "parentId") String parentId);
    List<ParentDiseaseInfoVo> getParentCodeByChildIdList(@Param(value = "childIdList") String childIdList,
                                                         @Param(value = "category") String category);
}
