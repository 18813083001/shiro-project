package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.model.dao.AiModelDao;
import com.thorough.core.modules.pathology.model.entity.AiModel;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.service.CommonService;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class AiModelService extends CommonService<String,AiModelDao,AiModel> {

    @Autowired
    ImageService imageService;

    /*
    * 根据iamgeId（获取hospital department）和diseaseId（根据分析级别获取染色方式、器官） 获取模型
    * @param diseaseId  简单分析或者复杂分析的id
    * */
    public AiModel getAiModelByImageIdAndLabelTypeId(String regionId){
        if (StringUtils.isBlank(regionId))
            return null;
        AiModel model = null;
        try {
            CommonExample example = new CommonExample(AiModel.class);
            example.createCriteria()
                    .andEqualTo(AiModel.getFieldDelFlag(),"0")
                    .andEqualTo(AiModel.getFieldDiseaseid(),regionId)
                    .andEqualTo(AiModel.getFieldAvailable(),"1");
            List<AiModel> list = this.selectByExample(example);
            if (list == null || list.size() == 0){
                return null;
            }
            else if (list != null && list.size() == 1){
                AiModel aiModel = list.get(0);
                model = aiModel;
            }else
                throw new CoreException("找到多个模型");

        }catch (Exception e){
            logger.error("查找AIModel:",e);
            e.printStackTrace();
        }
        return model;

    }

    public boolean haveModel(Disease data) {
        if (data == null)
            return false;
        //判断是否有预测模型
        CommonExample example = new CommonExample(AiModel.class);
        example.createCriteria().andEqualTo(AiModel.getFieldDiseaseid(),data.getId()).andEqualTo(AiModel.getFieldDelFlag(),"0");
        long count = this.countByExample(example);
        if (count > 0)
            return true;
        else return false;
    }
}
