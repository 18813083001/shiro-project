
package com.thorough.library.system.service;
import com.thorough.library.constant.Constant;
import com.thorough.library.specification.service.TreeService;
import com.thorough.library.system.model.dao.AreaDao;
import com.thorough.library.system.model.entity.Area;
import com.thorough.library.system.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 区域Service
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<String,AreaDao, Area> {

	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}

	@Transactional(readOnly = false)
	public void save(Area area) {
		super.save(area);
		UserUtils.removeCache(Constant.CACHE_AREA_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
		UserUtils.removeCache(Constant.CACHE_AREA_LIST);
	}
	
}
