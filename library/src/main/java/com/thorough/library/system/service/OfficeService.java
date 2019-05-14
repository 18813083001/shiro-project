
package com.thorough.library.system.service;

import com.thorough.library.constant.Constant;
import com.thorough.library.specification.service.TreeService;
import com.thorough.library.system.model.dao.OfficeDao;
import com.thorough.library.system.model.entity.Office;
import com.thorough.library.system.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 机构Service
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<String,OfficeDao, Office> {

	@Autowired
    OfficeDao officeDao;

	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll){
		if (isAll != null && isAll){
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList();
		}
	}

	public List<Office> findListByType(int type){
		return officeDao.findListByType(type);
	}
	
	@Transactional(readOnly = true)
	public List<Office> findList(Office office){
		if(office != null){
			office.setParentIds(office.getParentIds()+"%");
			return dao.findByParentIdsLike(office);
		}
		return  new ArrayList<Office>();
	}
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		super.save(office);
		UserUtils.removeCache(Constant.CACHE_OFFICE_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		UserUtils.removeCache(Constant.CACHE_OFFICE_LIST);
	}

	public List<Map> getHospitalIdList(){
		//医院
		List<Map> hospitalList = new ArrayList<>();
		List<Office> office = this.findListByType(1);
		if (office!=null && office.size() > 0){
			for (Office office1:office){
				Map map = new HashMap();
				map.put("id",office1.getId());
				map.put("name",office1.getName());
				map.put("code",office1.getCode());
				hospitalList.add(map);
			}

		}
		return hospitalList;
	}

	public List<String> getChildIdByParentId(String parentId){
		return officeDao.getChildIdByParentId(parentId);
	}
	
}
