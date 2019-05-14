package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.PatientDao;
import com.thorough.core.modules.pathology.model.entity.Patient;
import com.thorough.library.specification.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PatientService extends CommonService<String,PatientDao,Patient> {

    @Transactional(readOnly = false)
    public int insert(Patient patient){
        return super.insert(patient);
    }

    @Transactional(readOnly = false)
    public int deleteByPrimaryKey(String patientId){
        return super.deleteByPrimaryKey(patientId);
    }

    @Transactional(readOnly = false)
    public int updateByPrimaryKey(Patient patient){
        return super.updateByPrimaryKey(patient);
    }

}
