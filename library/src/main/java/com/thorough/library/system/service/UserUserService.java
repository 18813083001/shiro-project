package com.thorough.library.system.service;

import com.thorough.library.specification.service.BaseService;
import com.thorough.library.system.model.dao.UserUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class UserUserService implements BaseService {

    @Autowired
    UserUserDao userUserDao;

    public List<String> getUpperIdList(String userId){
        return userUserDao.getUpperIdList(userId);
    }

    public List<String> getBelongsIdList(String upperId){
        return userUserDao.getBelongsIdList(upperId);
    }

    public List<String> getAllUpperIdList(){
        return userUserDao.getAllUpperIdList();
    }


    @Transactional(readOnly = false)
    public int deleteRelation(List<String> imageIdList,String upperId){
        int num = 0;
        for (String userId:imageIdList){
            int row = userUserDao.deleteRelation(userId,upperId);
            num += row;
        }
        return num;
    }


    @Transactional(readOnly = false)
    public int addRelation(List<String> imageIdList,String upperId,String createBy){
        int num = 0;
        for (String userId:imageIdList){
            int row = userUserDao.addRelation(userId,upperId,createBy);
            num += row;
        }
        return num;

    }

}
