package com.thorough.core.modules.pathology.model.dao;

import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.ImageUser;
import com.thorough.core.modules.pathology.model.vo.StatisticsVo;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface ImageUserDao extends Dao {

    int insertSelective(ImageUser imageUser);
    int updateByImageIdUserIdSelective(ImageUser imageUser);
    List<Image> getImagesByUser(Image image);
    long getCountByUser(Image image);
    int updateOwnership(Map param);
    long count(Map param);
    List<String> getUserIdByImageId(String imageId);
    int getOwnership(@Param(value = "imageId") String imageId, @Param(value = "userId") String userId);
    List<String> getUserIdByImageIdAndReviewStage(@Param(value = "imageId") String imageId, @Param(value = "reviewStage") int reviewStage);
    int getReviewStageByImageIdAndUserId(@Param(value = "imageId") String imageId, @Param(value = "userId") String userId);
    long getCountByImageIdAndUserIdAndReviewStage(@Param(value = "imageId") String imageId, @Param(value = "userId") String userId,@Param(value = "reviewStage") int reviewStage);
    int deleteByImageIdAndUserId(@Param(value = "imageId") String imageId, @Param(value = "userId") String userId);
    int getMaxImageId();
    /**
     * 默认返回二审已经提交的数据
     * */
    List<Image> imageListToExpertAdvisor(Image image);
    long getCountByImageIdAndReviewStageAndOwnership(@Param(value = "imageId") String imageId, @Param(value = "ownership") int ownership,@Param(value = "reviewStage") int reviewStage);
    List<Image> imageAll(Image image);
}