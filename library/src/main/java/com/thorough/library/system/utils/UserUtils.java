package com.thorough.library.system.utils;

import com.thorough.library.constant.Constant;
import com.thorough.library.specification.system.Principal;
import com.thorough.library.system.model.dao.*;
import com.thorough.library.system.model.entity.*;
import com.thorough.library.system.model.vo.ParentDiseaseInfoVo;
import com.thorough.library.system.service.FrontMenuService;
import com.thorough.library.system.service.RoleFrontMenuService;
import com.thorough.library.system.session.Session;
import com.thorough.library.system.session.SessionManager;
import com.thorough.library.utils.ApplicationContextHolder;
import com.thorough.library.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;

import java.rmi.MarshalledObject;
import java.util.*;

public class UserUtils {
    private  static SessionManager sessionManager = ApplicationContextHolder.getBean(SessionManager.class);
    private static UserDao userDao = ApplicationContextHolder.getBean(UserDao.class);
    private static RoleDao roleDao = ApplicationContextHolder.getBean(RoleDao.class);
    private static MenuDao menuDao = ApplicationContextHolder.getBean(MenuDao.class);
    private static AreaDao areaDao = ApplicationContextHolder.getBean(AreaDao.class);
    private static OfficeDao officeDao = ApplicationContextHolder.getBean(OfficeDao.class);
    private static FrontMenuService frontMenuService = ApplicationContextHolder.getBean(FrontMenuService.class);
    private static RoleFrontMenuService roleFrontMenuService = ApplicationContextHolder.getBean(RoleFrontMenuService.class);
    private static RoleDiseaseDao roleDiseaseDao = ApplicationContextHolder.getBean(RoleDiseaseDao.class);
    private static UserUserDao userUserDao = ApplicationContextHolder.getBean(UserUserDao.class);

    /**
     * 根据登录名获取用户
     * @param loginName
     * @return 取不到返回null
     */
    public static User getByLoginName(String loginName){
        User user = (User) CacheUtils.get(Constant.USER_CACHE, Constant.USER_CACHE_LOGIN_NAME_ + loginName);
        if (user == null){
            user = userDao.getByLoginName(new User(null, loginName));
            if (user == null){
                return null;
            }
            user.setUserTypeName(DictUtils.getDictLabel(user.getUserType(),"sys_user_type",""));
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(Constant.USER_CACHE, Constant.USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(Constant.USER_CACHE, Constant.USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        }
        return user;
    }

    /**
     * 获取当前用户
     * @return 取不到返回 new User()
     */
    public static User getUser(){
        Principal principal = getPrincipal();
        if (principal!=null){
            User user = get(principal.getId());
            if (user != null){
                return user;
            }
            return new User();
        }
        // 如果没有登录，则返回实例化空的User对象。
        return new User();
    }

    /**
     * 根据ID获取用户
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id){
        User user = (User) CacheUtils.get(Constant.USER_CACHE, Constant.USER_CACHE_ID_ + id);
        if (user ==  null){
            user = userDao.get(id);
            if (user == null){
                return null;
            }
            user.setUserTypeName(DictUtils.getDictLabel(user.getUserType(),"sys_user_type",""));
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(Constant.USER_CACHE, Constant.USER_CACHE_ID_ + user.getId(), user);
            CacheUtils.put(Constant.USER_CACHE, Constant.USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
        }
        return user;
    }

    /**
     * 获取当前用户授权菜单
     * @return
     */
    public static List<Menu> getMenuList(){
        @SuppressWarnings("unchecked")
        List<Menu> menuList = (List<Menu>)getCache(Constant.CACHE_MENU_LIST);
        if (menuList == null){
            User user = getUser();
            if (user.isAdmin()){
                menuList = menuDao.findAllList(new Menu());
            }else{
                Menu m = new Menu();
                m.setUserId(user.getId());
                menuList = menuDao.findByUserId(m);
            }
            putCache(Constant.CACHE_MENU_LIST, menuList);
        }
        return menuList;
    }

    /**
     * 获取当前登录者对象
     */
    public static Principal getPrincipal(){
        try{
            Subject subject = SecurityUtils.getSubject();
            Principal principal = (Principal)subject.getPrincipal();
            if (principal != null){
                return principal;
            }
        }catch (UnavailableSecurityManagerException e) {
            e.printStackTrace();
        }catch (InvalidSessionException e){
            e.printStackTrace();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前用户授权的区域
     * @return
     */
    public static List<Area> getAreaList(){
        @SuppressWarnings("unchecked")
        List<Area> areaList = (List<Area>)getCache(Constant.CACHE_AREA_LIST);
        if (areaList == null){
            areaList = areaDao.findAllList(new Area());
            putCache(Constant.CACHE_AREA_LIST, areaList);
        }
        return areaList;
    }

    /**
     * 获取当前用户有权限访问的部门
     * @return
     */
    public static List<Office> getOfficeList(){
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>)getCache(Constant.CACHE_OFFICE_LIST);
        if (officeList == null){
            User user = getUser();
            if (user.isAdmin()){
                officeList = officeDao.findAllList(new Office());
            }else{
                Office office = new Office();
                office.setId(user.getOfficeId());
                officeList = officeDao.findList(office);
            }
            putCache(Constant.CACHE_OFFICE_LIST, officeList);
        }
        return officeList;
    }

    /**
     * 获取当前用户有权限访问的部门
     * @return
     */
    public static List<Office> getOfficeAllList(){
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>)getCache(Constant.CACHE_OFFICE_ALL_LIST);
        if (officeList == null){
            officeList = officeDao.findAllList(new Office());
        }
        return officeList;
    }

    /**
     * 获取当前用户角色列表
     * @return
     */
    public static List<Role> getRoleList(){
        @SuppressWarnings("unchecked")
        List<Role> roleList = (List<Role>)getCache(Constant.CACHE_ROLE_LIST);
        if (roleList == null){
            User user = getUser();
            if (user.isAdmin()){
                roleList = roleDao.findAllList(new Role());
            }else{
                Role role = new Role();
                roleList = roleDao.findList(role);
            }
            putCache(Constant.CACHE_ROLE_LIST, roleList);
        }
        return roleList;
    }

    /**
     *
     * 获取当前用户授权前端菜单
     */
    public static List<FrontMenu> getFrontMenuList(){
        List<FrontMenu> frontMenuList = (List<FrontMenu>)getCache(Constant.CACHE_FRONT_MENU_LIST);
        if(frontMenuList == null){
            User user = getUser();
            if (user.isAdmin()){
                frontMenuList = frontMenuService.getAllFrontMenu();
            }else {
                List<String> roleIds = user.getRoleIdList();
                if (roleIds != null){
                    frontMenuList = roleFrontMenuService.getFrontMenuIdsByRoleIds(roleIds);
                }
            }
            putCache(Constant.CACHE_FRONT_MENU_LIST, frontMenuList);
        }
        return frontMenuList;
    }

    public static List<ParentDiseaseInfoVo> getParentCodeByChildIdList(Set<String> diseaseIdList, String category){
        if (diseaseIdList == null)
            return null;
        List<ParentDiseaseInfoVo> diseaseIdOrganCodeMap = (List<ParentDiseaseInfoVo>) getCache(Constant.CACHE_USER_DISEASE_ORGAN_CODE_MAP);
        if (diseaseIdOrganCodeMap == null){
            String diseaseIdListString = "";
            Iterator<String> iterator = diseaseIdList.iterator();
            while (iterator.hasNext()){
                diseaseIdListString += iterator.next();
                if (iterator.hasNext()){
                    diseaseIdListString += ",";
                }
            }
            if (StringUtils.isNotBlank(diseaseIdListString)){
                List<ParentDiseaseInfoVo> list = roleDiseaseDao.getParentCodeByChildIdList(diseaseIdListString,category);
                putCache(Constant.CACHE_USER_DISEASE_ORGAN_CODE_MAP,list);
                return diseaseIdOrganCodeMap;
            }else
                return null;
        }else{
            Iterator<String> iterable = diseaseIdList.iterator();
            Set noIdOrganCodeSet = new HashSet();
            while (iterable.hasNext()){
                String diseaseId = iterable.next();
                boolean exist = false;
                for(ParentDiseaseInfoVo diseaseInfoVo:diseaseIdOrganCodeMap){
                    if (diseaseInfoVo.getChildId().equals(diseaseId)){
                        exist = true;
                        break;
                    }
                }
                //如果不存在
                if (!exist){
                    noIdOrganCodeSet.add(diseaseId);
                }
            }
            if (noIdOrganCodeSet.size() > 0){
                String diseaseIdListString = null;
                Iterator<String> noIdCodes = noIdOrganCodeSet.iterator();
                while (noIdCodes.hasNext()){
                    diseaseIdListString += noIdCodes.next();
                    if (noIdCodes.hasNext()){
                        diseaseIdListString += ",";
                    }
                }
                List<ParentDiseaseInfoVo> list = roleDiseaseDao.getParentCodeByChildIdList(diseaseIdListString,category);
                diseaseIdOrganCodeMap.addAll(list);
                putCache(Constant.CACHE_USER_DISEASE_ORGAN_CODE_MAP,diseaseIdOrganCodeMap);
            }
            return diseaseIdOrganCodeMap;
        }
    }

    public static List<Map<String,String>> getUserNameListByUserIdList(Collection<String> set){
        if (set == null)
            return null;
        List<Map<String,String>> userIdNameMap = (List<Map<String, String>>) getCache(Constant.CACHE_USER_ID_NAME_MAP);
        if (userIdNameMap == null){
            userIdNameMap = userDao.getUserNameListByUserIdList(set);
            putCache(Constant.CACHE_USER_ID_NAME_MAP,userIdNameMap);
            return userIdNameMap;
        }else{
            List<Map<String,String>> newUserIdNameMap = new LinkedList<>();
            Iterator<String> iterable = set.iterator();
            Set noIdNameSet = new HashSet();
            while (iterable.hasNext()){
                String userId = iterable.next();
                if (StringUtils.isBlank(userId))
                    continue;
                Map existMap = null;
                for (Map<String,String> map:userIdNameMap){
                    if (map.get("id").equals(userId)){
                        existMap = map;
                        break;
                    }
                }
                if (existMap != null)
                    newUserIdNameMap.add(existMap);
                else noIdNameSet.add(userId);
            }

            if (noIdNameSet.size() > 0){
                List<Map<String,String>> otherIdNameMap = userDao.getUserNameListByUserIdList(noIdNameSet);
                newUserIdNameMap.addAll(otherIdNameMap);
                userIdNameMap.addAll(otherIdNameMap);
            }
            if (noIdNameSet.size() > 0)
                putCache(Constant.CACHE_USER_ID_NAME_MAP,userIdNameMap);
            return newUserIdNameMap;
        }
    }

    /**
     * 获取当前用户的所属用户id-name
     * @return
     */
    public static List<Map<String,String>> getBelongsUserNameList(){
        List<Map<String,String>> belongsUserIdNameMap = (List<Map<String, String>>) getCache(Constant.CACHE_BELONG_USER_ID_NAME_MAP);
        if (belongsUserIdNameMap == null){
            List<String> belongUserId = getBelongUserIds();
            Set idSet = new HashSet();
            if (belongUserId != null && belongUserId.size() > 0){
                idSet.addAll(belongUserId);
            }
            belongsUserIdNameMap = userDao.getUserNameListByUserIdList(idSet);
            putCache(Constant.CACHE_BELONG_USER_ID_NAME_MAP,belongsUserIdNameMap);
        }
        return belongsUserIdNameMap;
    }

    public static List<Map<String,String>> getUserIdAndNameExceptUserId(List<String> exceptUserIdList){
        List<Map<String,String>> belongsUserIdNameMap = userDao.getUserIdAndNameExceptUserId(exceptUserIdList);
        return belongsUserIdNameMap;
    }
    
    public static List<Map<String,String>> getUserIdNameTypeByHospitalId(String hospitalId,String userType){
        List<Map<String,String>> userIdNameTypeList = userDao.getUserIdNameTypeByHospitalId(hospitalId,userType);
        return userIdNameTypeList;
    }

    public static List<String> getUserIdListFromUserMapList(List<Map<String,String>> userListByHospital){
        List<String> userIdList = new ArrayList<>();
        if (userListByHospital != null)
            for (Map map :userListByHospital){
                userIdList.add((String) map.get("id"));
            }
        return userIdList;
    }

    /**
     * 获取当前用户的所属用户
     * @return
     */
    public static List<String> getBelongUserIds(){
        List<String> belongIdList = (List<String>)getCache(Constant.CACHE_BELONG_ID_LIST);
        if (belongIdList == null){
            if (UserUtils.getUser().isAdmin()){
                belongIdList = userDao.getAllUserId();
            } else {
                belongIdList = userUserDao.getBelongsIdList(getUser().getId());
            }
            putCache(Constant.CACHE_BELONG_ID_LIST,belongIdList);
        }
        return belongIdList;
    }




    /**
     * 清除当前用户缓存
     */
    public static void clearCache(){
        removeCache(Constant.USER_CACHE_AUTH_INFO);
        removeCache(Constant.CACHE_ROLE_LIST);
        removeCache(Constant.CACHE_MENU_LIST);
        removeCache(Constant.CACHE_FRONT_MENU_LIST);
        removeCache(Constant.CACHE_AREA_LIST);
        removeCache(Constant.CACHE_OFFICE_LIST);
        removeCache(Constant.CACHE_OFFICE_ALL_LIST);
        removeCache(Constant.CACHE_DISEASE_LIST);
        removeCache(Constant.CACHE_BELONG_ID_LIST);
        removeCache(Constant.CACHE_BELONG_USER_ID_NAME_MAP);
        UserUtils.clearCache(getUser());
    }

    /**
     * 清除指定用户缓存
     * @param user
     */
    public static void clearCache(User user){
        CacheUtils.remove(Constant.USER_CACHE, Constant.USER_CACHE_ID_ + user.getId());
        CacheUtils.remove(Constant.USER_CACHE, Constant.USER_CACHE_LOGIN_NAME_ + user.getLoginName());
        CacheUtils.remove(Constant.USER_CACHE, Constant.USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
    }

    /**
     * 获取授权主要对象
     */
    public static Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    // ============== User Cache ==============

    public static Object getCache(String key) {
        return getCache(key, null);
    }

    public static Object getCache(String key, Object defaultValue) {
        Object obj = getSession().getAttribute(key);
        return obj==null?defaultValue:obj;
    }

    public static void putCache(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static void removeCache(String key) {
        getSession().removeAttribute(key);
    }

    public static Session getSession(){
        return sessionManager.getSession();
    }
}
