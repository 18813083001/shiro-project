package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.ImageCustomDao;
import com.thorough.library.specification.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ImageCustomService implements BaseService {
    @Autowired
    private ImageCustomDao imageCustomDao;

    List<String> selectImageIdListByHospitalIdAndDiseaseId(String hospitalId, String diseaseId){
        return imageCustomDao.selectImageIdListByHospitalIdAndDiseaseId(hospitalId,diseaseId);
    }

    List<String> selectImageIdListByHospitalIdAndDiseaseIdFromPool(String hospitalId, String diseaseId,int reviewStage,String userId){
        return imageCustomDao.selectImageIdListByHospitalIdAndDiseaseIdFromPool(hospitalId,diseaseId,reviewStage,userId);
    }

}
