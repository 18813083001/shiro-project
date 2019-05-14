package com.thorough.library.system.service;

import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.service.CommonService;
import com.thorough.library.system.model.dao.DiseaseDao;
import com.thorough.library.system.model.dao.RoleDiseaseDao;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional(readOnly = false)
public abstract class DiseaseService extends CommonService<String,DiseaseDao,Disease> {


    @Autowired
    RoleDiseaseDao roleDiseaseDao;

    @Transactional(readOnly = false)
    public String save(Disease disease){
        //兼容后台管理系统的更新操作
        try {
            if(StringUtils.isNotBlank(disease.getId())){
                if (disease.getSort() == 0){
                    Disease sort = this.selectByPrimaryKey(disease.getParentId());
                    if (sort != null){
                        disease.setSort(sort.getSort());
                    }
                }
                this.updateByPrimaryKeySelective(disease);
            }else{
                if(StringUtils.isNotBlank(disease.getParentId()) && !disease.getParentId().equals("0")) {
                    Disease parent = this.selectByPrimaryKey(disease.getParentId());
                    if (parent != null)
                        disease.setParentIds(parent.getParentIds()+parent.getId()+",");
                }
                else {
                    disease.setParentId("0");
                    disease.setParentIds("0,");
                }
                this.insert(disease);
            }
            UserUtils.removeCache(Constant.CACHE_ORGAN);
        }catch (Exception e){
            throw e;
        }
        return disease.getId();
    }

    @Transactional(readOnly = false)
    public int update(Disease disease){
        int row = 0;
        if(StringUtils.isNotBlank(disease.getId())){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            //前端传的sort值为空,或者前端没有传sort字段
            if (disease.getSort() == null || StringUtils.isBlank(request.getParameter("sort"))){
                Disease sort = this.selectByPrimaryKey(disease.getId());
                if (sort != null){
                    disease.setSort(sort.getSort());
                }
            }
            row = this.updateByPrimaryKeySelective(disease);
            if (row > 0)
                UserUtils.removeCache(Constant.CACHE_ORGAN);
        }
        return row;
    }

    @Transactional(readOnly = false)
    public int delete(Disease disease){
        CommonExample example = new CommonExample(Disease.class);
        disease.setDelFlag("1");
        example.createCriteria().andEqualTo(Disease.getFieldId(),disease.getId());
        example.or().andLike(Disease.getFieldParentIds(),"%,"+disease.getId()+",%");
        //防止update时出现update pathology_disease SET id = 导致duplicate异常
        disease.setId(null);
        int row = this.updateByExampleSelective(disease,example);
        if (row > 0)
            UserUtils.removeCache(Constant.CACHE_ORGAN);
        return row;
    }

    @Transactional(readOnly = false)
    public List<Map> treeData(Disease disease){
        List<Disease> all = this.getChildsByParentIdAndUserId(disease.getParentId(), UserUtils.getUser().getId(),null);
        List<Map> treeData = null;
        if(StringUtils.isNotBlank(disease.getParentId()))
            treeData = treeData(all,disease.getParentId());
        else treeData = treeData(all,"0");
        return treeData;
    }


    private List<Map> treeData(List<Disease> data, String id){
        if(data == null)
            return null;
        List<Map> child = new ArrayList<>();
        for(int i =0;i < data.size();i++){
            if(data.get(i).getParentId().equals(id)){
                List<Map> child2 = treeData(data,data.get(i).getId());
                //实体对象
                Map<String,Object> map = new HashMap();
                map.put("id",data.get(i).getId());
                map.put("pid",data.get(i).getParentId());
                map.put("name",data.get(i).getName());
                map.put("rgb",data.get(i).getRgb());
                map.put("code",data.get(i).getCode());
                map.put("anchor",data.get(i).getAnchor());
                map.put("category",data.get(i).getCategory());
                if ("labelType".equals(data.get(i).getCategory())){
                    if (haveModel(data.get(i)))
                        map.put("haveModel",true);
                    else
                        map.put("haveModel",false);
                }
                if (!"labelType".equals(data.get(i).getCategory()) || !"disease".equals(data.get(i).getCategory())){
                    long imageCount = statisticsImageCountByDiseaseId(data.get(i).getId(),1);
                    map.put("imageCount",imageCount);
                }
                map.put("childs",child2);
                child.add(map);
            }
        }
        return child;
    }


    @Transactional(readOnly = true)
    public List<Disease> findList(Disease disease){
        if(disease != null){
            String ids = disease.getParentIds()==null?"":disease.getParentIds();
            disease.setParentIds(ids+"%");
            CommonExample example = new CommonExample(Disease.class);
            example.createCriteria().andLike(Disease.getFieldParentIds(),disease.getParentIds()).andEqualTo(Disease.getFieldDelFlag(),"0");
            example.setOrderByClause("sort asc");
            return this.selectByExample(example);
        }
        return  new ArrayList<Disease>();
    }

    /*
    * 获取parentId的直属子数据
    * */
    public List<Disease> findDirectChildsListByParentId(String parentId){
        CommonExample example = new CommonExample(Disease.class);
        CommonExample.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Disease.getFieldParentId(),parentId);
        criteria.andEqualTo(Disease.getFieldDelFlag(),"0");
        example.setOrderByClause("sort asc");
        List<Disease> list = this.selectByExample(example);
        return list;
    }

    @Transactional(readOnly = false)
    public List<String> getAllChildIdsByParentIdAndUserId(String parentId,String userId,String category){
        List<Disease> list = this.getChildsByParentIdAndUserId(parentId,userId,category);
        List<String> ids = new ArrayList<>();
        for(int i=0;i < list.size();i++){
            ids.add(list.get(i).getId());
        }
        return ids;
    }

    public List<String> getDiseaseChildIdsByParentId(String parentId){
        if (StringUtils.isBlank(parentId))
            return null;
        List<String> diseaseIds = roleDiseaseDao.getDiseaseChildIdsByParentId(parentId);
        return diseaseIds;
    }

    public List<String> diseaseToListId(List<Disease> list){
        List<String> ids = new ArrayList<>();
        for(int i=0;i < list.size();i++){
            ids.add(list.get(i).getId());
        }
        return ids;
    }

    @Transactional(readOnly = false)
    public List<String> getChildsByParentIdAndCategory(String parentId,String category){
        List<String> diseaseIds = roleDiseaseDao.getDiseaseChildIdsByParentId(parentId);
        List<Disease> diseaseList = roleDiseaseDao.getChildsFromDiseaseIdListAndCategory(diseaseIds,category);
        List<String> idList = new ArrayList<>();
        if (diseaseList != null){
            for (Disease disease:diseaseList){
                idList.add(disease.getId());
            }
        }
        return idList;
    }

    public List<Disease> findAll(){
        CommonExample example = new CommonExample(Disease.class);
        example.createCriteria().andEqualTo(Disease.getFieldDelFlag(),"0");
        return this.selectByExample(example);
    }

    public boolean exist(String  id){
        CommonExample example = new CommonExample(Disease.class);
        example.createCriteria().andEqualTo(Disease.getFieldDelFlag(),"0").andEqualTo(Disease.getFieldId(),id);
        long count = this.countByExample(example);
        if(count == 1 )
            return true;
        else return false;
    }

    public List<String> getDiseaseIdsByUserId(String userId){
        User user = UserUtils.get(userId);
        List<String> roleIds = user.getRoleIdList();
        return getDiseaseIdsByRoleId(roleIds);
    }

    public List<String> getDiseaseIdsByRoleId(List<String> roleIds){
        return roleDiseaseDao.getDiseaseIdsByRoleIds(roleIds);
    }

    @Transactional(readOnly = false)
    public int insertRoleDisease(String roleId,String diseaseIds){
        if(StringUtils.isNotBlank(roleId)){
            roleDiseaseDao.deleteRoleDisease(roleId);
            List<String> ids = Arrays.asList(diseaseIds.split(","));
            Map<String,Object> param = new HashMap<>();
            param.put("roleId",roleId);
            param.put("diseaseList",ids);
            return roleDiseaseDao.insertRoleDisease(param);
        }else throw new NullPointerException("roleId or diseaseIds is empty");
    }

    @Transactional(readOnly = false)
    public List<Disease> getDirectChildsByParentIdAndRoleIds(String parentId){
        List<String> roleIds = UserUtils.getUser().getRoleIdList();
        Map<String,Object> param = new HashMap<>();
        param.put("parentId",parentId);
        param.put("roleIds",roleIds);
        return roleDiseaseDao.getDirectChildsByParentIdAndRoleId(param);
    }

    @Transactional(readOnly = false)
    public List<Disease> getDirectChildsByUserIdAndParentIdAndRoleIds(String userId, String parentId){
        List<String> roleIds = UserUtils.get(userId).getRoleIdList();
        Map<String,Object> param = new HashMap<>();
        param.put("parentId",parentId);
        param.put("roleIds",roleIds);
        return roleDiseaseDao.getDirectChildsByParentIdAndRoleId(param);
    }


    public  List<Disease> getDirectChildsByParentIdAndExcludeIds(String parentId, List<String> excludeId){
        CommonExample example = new CommonExample(Disease.class);
        if(excludeId.size() == 0){
            example.createCriteria().andEqualTo(Disease.getFieldParentId(),parentId).andEqualTo(Disease.getFieldDelFlag(),"0");
        }else {
            example.createCriteria().andEqualTo(Disease.getFieldParentId(),parentId).andNotIn(Disease.getFieldId(),excludeId).andEqualTo(Disease.getFieldDelFlag(),"0");;
        }
        example.setOrderByClause("code asc");
        List<Disease> list = this.selectByExample(example);
        return list;
    }

    @Transactional(readOnly = false)
    public List<Disease> getChildsByParentIdAndCategoryAndRoleIds(String parentId, String category){
        List<String> roleIds = UserUtils.getUser().getRoleIdList();
        List<String> diseaseIds = roleDiseaseDao.getDiseaseChildIdsByParentId(parentId);
        return roleDiseaseDao.getChildsFromDiseaseIdListAndCategoryAndRole(diseaseIds,category,roleIds);
    }

    @Transactional(readOnly = false)
    public List<Disease> getChildsByParentIdAndUserId(String parentId, String userId, String category){
        List<String> roleIds = UserUtils.get(userId).getRoleIdList();
        List<String> diseaseIds = roleDiseaseDao.getDiseaseChildIdsByParentId(parentId);
        return roleDiseaseDao.getChildsFromDiseaseIdListAndCategoryAndRole(diseaseIds,category,roleIds);
    }


    @Transactional(readOnly = false)
    public List<Map> getDirectDiseaseListByParentIdAndCategoryAndRole(Disease disease,int aiPredict){
        List<Disease> list = new ArrayList<>();
        if (disease == null)
            return null;
        if(StringUtils.isBlank(disease.getParentId()))
            disease.setParentId("0");
        if(StringUtils.isNotBlank(disease.getParentId()) && StringUtils.isBlank(disease.getCategory())){
            //通过父id获取直属配置项
            list = this.getDirectChildsByParentIdAndRoleIds(disease.getParentId());
        }else if(StringUtils.isNotBlank(disease.getParentId()) && StringUtils.isNotBlank(disease.getCategory())){
            //通过父id和category获取直属配置项
            list = this.getChildsByParentIdAndCategoryAndRoleIds(disease.getParentId(),disease.getCategory());
        }
        return diseaseToMap(list,true,aiPredict);
    }

    /*
    * 获取可用的和所有的器官信息，并缓存到当前session
    * */
    public Map<String,Object> getAvailableAndUnavailableDirectDiseaseListByParentId(String parentId,int aiPredict){
        Map<String,Object> map = null;
        map = (Map<String, Object>) UserUtils.getSession().getAttribute(Constant.CACHE_ORGAN);
        if (map == null){
            List<Map> availableDiseaseMap;
            List<Disease> list;
            if(StringUtils.isBlank(parentId))
                parentId = "0";
            User user = UserUtils.getUser();
            //如果是管理员，获取所有配置项数据
            if(user.isAdmin()){
                list = this.findDirectChildsListByParentId(parentId);
            }else {
                //通过父id获取直属配置项
                list = this.getDirectChildsByParentIdAndRoleIds(parentId);
            }
            availableDiseaseMap = diseaseToMap(list,true,aiPredict);

            List<String> ids = this.diseaseToListId(list);
            List<Disease> remainderDiseaseList = this.getDirectChildsByParentIdAndExcludeIds(parentId,ids);
            List<Map> unavailableDiseaseMap;
            unavailableDiseaseMap = diseaseToMap(remainderDiseaseList,false,aiPredict);

            List<Map> result = new ArrayList<>();
            if (availableDiseaseMap != null)
                result.addAll(availableDiseaseMap);
            if (unavailableDiseaseMap != null)
                result.addAll(unavailableDiseaseMap);

            map = new HashMap<>();
            map.put("total",result.size());
            map.put("availableSize",availableDiseaseMap!=null?availableDiseaseMap.size():0);
            map.put("list",result);
            UserUtils.putCache(Constant.CACHE_ORGAN,map);
        }
        return map;
    }

    public List<Map> getDirectOrganListByParentId(String parentId,int aiPredict){
        List<Map> organList;
        List<Disease> list;
        //通过父id获取直属配置项
        list = this.findDirectChildsListByParentId(parentId);
        organList = diseaseToMap(list,true,aiPredict);
        return organList;
    }

    public List<Map> getDirectAvailableDiseaseListByUserIdParentId(String userId,String parentId,int aiPredict){
        List<Map> availableDiseaseMap;
        List<Disease> list;
        //通过父id获取直属配置项
        list = this.getDirectChildsByUserIdAndParentIdAndRoleIds(userId,parentId);
        availableDiseaseMap = diseaseToMap(list,true,aiPredict);
        return availableDiseaseMap;
    }

    public List<Map> getDirectAvailableDiseaseListByUserIdListAndParentId(List<String> userIdList,String parentId,int aiPredict){
        if (userIdList == null || userIdList.size() == 0)
            return new ArrayList<>();
        if (StringUtils.isBlank(parentId))
            return new ArrayList<>();
        Set<Map> all = new HashSet<>();
        for (String userId:userIdList){
            List<Map> availableDiseaseMap = getDirectAvailableDiseaseListByUserIdParentId(userId,parentId,aiPredict);
            all.addAll(availableDiseaseMap);
        }
        List list = new ArrayList();
        list.addAll(all);
        return list;
    }

    public Map getParentByChildId(String diseaseId,String category){
        if (StringUtils.isBlank(diseaseId)|| StringUtils.isBlank(category))
            return null;
        String key = diseaseId+"-"+category;
        Map cacheMap = (Map) UserUtils.getCache(key);
        if (cacheMap != null)
            return cacheMap;
        Disease disease = this.selectByPrimaryKey(diseaseId);
        Map map = new HashMap();
        String[] parentIds = disease.getParentIds()!=null?disease.getParentIds().split(","):null;
        //默认直接取分类id
        if (parentIds!=null){
            if (parentIds.length >=5 ){
                String id = parentIds[3];
                Disease regionDisease = this.selectByPrimaryKey(id);
                if (category.equals(regionDisease.getCategory())){
                    map.put("id",regionDisease.getId());
                    map.put("name",regionDisease.getName());
                    map.put("code",regionDisease.getCode());
                }
            }
        }
        if (map.size() > 0){
            UserUtils.putCache(key,map);
            return map;
        }
        else {
            //如果没有找到，则遍历所有的配置项id
            if (parentIds != null){
                for (String id:parentIds){
                    Disease regionDisease = this.selectByPrimaryKey(id);
                    if (regionDisease!=null)
                    if (category.equals(regionDisease.getCategory())){
                        map.put("id",regionDisease.getId());
                        map.put("name",regionDisease.getName());
                        map.put("code",regionDisease.getCode());
                        UserUtils.putCache(key,map);
                        break;
                    }
                }
            }
        }
        return map;
    }

    private List<Map> diseaseToMap(List<Disease> data, boolean available,int aiPredict){
        if(data == null)
            return null;
        List<Map> list = new ArrayList<>();
        for(int i =0;i < data.size();i++){
            Map<String,Object> map = new HashMap();
            map.put("id",data.get(i).getId());
            map.put("pid",data.get(i).getParentId());
            map.put("name",data.get(i).getName());
            map.put("rgb",data.get(i).getRgb());
            map.put("code",data.get(i).getCode());
            map.put("sort",data.get(i).getSort());
            map.put("anchor",data.get(i).getAnchor());
            map.put("category",data.get(i).getCategory());
            map.put("available",available);
            if ("labelType".equals(data.get(i).getCategory())){
                if (haveModel(data.get(i)))
                    map.put("haveModel",true);
                else map.put("haveModel",false);
            }
            if (available){
                if ("labelType".equals(data.get(i).getCategory()) || "disease".equals(data.get(i).getCategory())){
                    map.put("imageCount",0);
                }else {
                    long imageCount = statisticsImageCountByDiseaseId(data.get(i).getId(),aiPredict);
                    map.put("imageCount",imageCount);
                }
            }
            list.add(map);
        }
        return list;
    }

    public abstract long statisticsImageCountByDiseaseId(String diseaseId,int aiPredict);

    public abstract boolean haveModel(Disease data);

    @Transactional(readOnly = false)
    public void addRegion(){
        //找到所有的染色方式数据
        CommonExample example = new CommonExample(Disease.class);
        example.createCriteria().andEqualTo(Disease.getFieldCategory(),"dyeing").andEqualTo(Disease.getFieldDelFlag(),"0");
        List<Disease> dyeings = this.selectByExample(example);
        if (dyeings != null && dyeings.size() > 0){
            for(int i =0;i < dyeings.size();i++){
                Disease dyeing = dyeings.get(i);
                //查找染色方式的所有标注类型
                example = new CommonExample(Disease.class);
                example.createCriteria().andEqualTo(Disease.getFieldParentId(),dyeing.getId()).andEqualTo(Disease.getFieldDelFlag(),"0");
                List<Disease> labelTypeDiseases = this.selectByExample(example);

                //插入一条分类数据
                Disease region = new Disease();
                region.setParentId(dyeing.getId());
                region.setParentIds(dyeing.getParentIds() +dyeing.getId()+",");
                region.setCategory("region");
                region.setCode("default");
                region.setName("默认分类");
                region.setRgb("#ffffff");
                this.insert(region);

                if (labelTypeDiseases != null && labelTypeDiseases.size() > 0){
                    for(int j = 0;j < labelTypeDiseases.size();j++){
                        Disease labelTypeDisease = labelTypeDiseases.get(j);
                        labelTypeDisease.setParentId(region.getId());
                        labelTypeDisease.setParentIds(region.getParentIds()+region.getId()+",");
                        this.updateByPrimaryKeySelective(labelTypeDisease);
                        //找到标注下的所有疾病类型
                        example = new CommonExample(Disease.class);
                        example.createCriteria().andEqualTo(Disease.getFieldParentId(),labelTypeDisease.getId()).andEqualTo(Disease.getFieldDelFlag(),"0");
                        List<Disease> diseaseList = this.selectByExample(example);
                        if (diseaseList != null && diseaseList.size() > 0){
                            for (int n = 0; n < diseaseList.size();n++){
                                Disease disease = diseaseList.get(n);
                                disease.setParentIds(labelTypeDisease.getParentIds()+labelTypeDisease.getId()+",");
                                this.updateByPrimaryKeySelective(disease);
                            }
                        }
                    }
                }
            }
        }
    }

    public List<String> getDiseaseIdListByUserIdList(List userIdList,String category){
        if (userIdList != null && userIdList.size() > 0)
            return roleDiseaseDao.getDiseaseIdListByUserIdList(userIdList,category,null);
        else return new ArrayList<>();
    }

    public List<String> getDiseaseIdListByUserIdList(List userIdList,String category,String parentId){
        if (userIdList != null && userIdList.size() > 0)
        return roleDiseaseDao.getDiseaseIdListByUserIdList(userIdList,category,parentId);
        else return new ArrayList<>();
    }


}
