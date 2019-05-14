package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.DiseaseCustomDao;
import com.thorough.library.system.model.entity.Disease;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = false)
public class DiseaseCustomService {
    @Autowired
    DiseaseCustomDao diseaseCustomDao;

    public List<Disease> selectImageIdListByHospitalIdAndDiseaseId(String diseaseId){
       return diseaseCustomDao.selectImageIdListByHospitalIdAndDiseaseId(diseaseId);
    }

    public List<Map<String,String>> getDiseaseByDiseaseIdList(Collection<String> set){
        return diseaseCustomDao.getDiseaseByDiseaseIdList(set);
    }
}
