
package com.thorough.library.system.service;

import com.thorough.library.constant.Constant;
import com.thorough.library.specification.service.CrudService;
import com.thorough.library.system.model.dao.DictDao;
import com.thorough.library.system.model.entity.Dict;
import com.thorough.library.system.utils.CacheUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典Service
 */
@Service
@Transactional(readOnly = true)
public class DictService extends CrudService<String,DictDao, Dict> {
	
	/**
	 * 查询字段类型列表
	 * @return
	 */
	public List<String> findTypeList(){
		return dao.findTypeList(new Dict());
	}

	@Transactional(readOnly = false)
	public void save(Dict dict) {
		super.save(dict);
		CacheUtils.remove(Constant.CACHE_DICT_MAP);
	}

	@Transactional(readOnly = false)
	public void delete(Dict dict) {
		super.delete(dict);
		CacheUtils.remove(Constant.CACHE_DICT_MAP);
	}

}
