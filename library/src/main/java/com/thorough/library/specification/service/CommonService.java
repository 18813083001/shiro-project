package com.thorough.library.specification.service;

import com.thorough.library.mybatis.persistence.model.dao.CommonDao;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.mybatis.persistence.model.entity.CommonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public class CommonService<P,D extends CommonDao<P,T>, T extends CommonEntity<P>> implements BaseService {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 持久层对象
     */
    @Autowired
    protected D dao;

    public long countByExample(CommonExample example){
        try {
            return dao.countByExample(example);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public long deleteByExample(CommonExample example){
        try {
            return dao.deleteByExample(example);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int deleteByPrimaryKey(P id){
        try {
            return dao.deleteByPrimaryKey(id);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int insert(T record){
        try {
            record.preInsert();
            return dao.insert(record);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int insertSelective(T record){
        try {
            record.preInsert();
            return dao.insertSelective(record);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public List<T> selectByExample(CommonExample example){
        try {
            return dao.selectByExample(example);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public T selectByPrimaryKey(P id){
        try {
            return dao.selectByPrimaryKey(id);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int updateByExampleSelective(T record, CommonExample example){
        try {
            record.preUpdate();
            return dao.updateByExampleSelective(record,example);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int updateByExample(T record, CommonExample example){
        try {
            record.preUpdate();
            return dao.updateByExample(record,example);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int updateByPrimaryKeySelective(T record){
        try {
            record.preUpdate();
            return dao.updateByPrimaryKeySelective(record);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public int updateByPrimaryKey(T record){
        try {
            record.preUpdate();
            return dao.updateByPrimaryKey(record);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
