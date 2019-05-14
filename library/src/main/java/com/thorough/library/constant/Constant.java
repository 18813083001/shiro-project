package com.thorough.library.constant;


import com.thorough.library.utils.PropertyUtil;

public class Constant {
    public static final String APP_CACHE = PropertyUtil.getProperty("adminPath")+"_library_";
    public static final String CATEGORY_ORGAN = "organ";
    public static final String CATEGORY_DYEING = "dyeing";
    public static final String CATEGORY_RIGION = "region";
    public static final String CATEGORY_LABELTYPE = "labelType";
    public static final String CATEGORY_DISEASE = "disease";

    /*kafka topic*/
    public static final String KAFKA_TOPIC_CANCEL = "cancel";
    public static final String KAFKA_TOPIC_PRIORITY = "priority";

    public static final String VALIDATE_CODE = "validateCode";

    /*redis缓存使用*/
    public static final String CACHE_DICT_MAP = Constant.APP_CACHE+"dictMap";
    public static final String USER_CACHE = Constant.APP_CACHE+"userCache";
    public static final String USER_CACHE_ID_ = Constant.APP_CACHE+"id_";
    public static final String USER_CACHE_LOGIN_NAME_ = Constant.APP_CACHE+"ln_";
    public static final String USER_CACHE_LIST_BY_OFFICE_ID_ =  Constant.APP_CACHE+"oid_";
    public static final String USER_CACHE_LIST_BY_COMPANY_ID_ =  Constant.APP_CACHE+"cid_";
    public static final String USER_CACHE_AUTH_INFO =  Constant.APP_CACHE+"authInfo";

    public static final String CACHE_USER_ID_NAME_MAP =  Constant.APP_CACHE+"userIdNameMap";
    public static final String CACHE_USER_DISEASE_ORGAN_CODE_MAP =  Constant.APP_CACHE+"diseaseId_OrganCode_Map";
    public static final String CACHE_BELONG_USER_ID_NAME_MAP =  Constant.APP_CACHE+"belongUserIdNameMap";
    public static final String CACHE_BELONG_ID_LIST =  Constant.APP_CACHE+"belongIds";
    public static final String CACHE_UPPER_ID_LIST = Constant.APP_CACHE+"upperIds";
    public static final String CACHE_ROLE_LIST =  Constant.APP_CACHE+"roleList";
    public static final String CACHE_MENU_LIST =  Constant.APP_CACHE+"menuList";
    public static final String CACHE_FRONT_MENU_LIST =  Constant.APP_CACHE+"frontMenuList"; //用户拥有的前端菜单
    public static final String CACHE_AREA_LIST =  Constant.APP_CACHE+"areaList";
    public static final String CACHE_OFFICE_LIST =  Constant.APP_CACHE+"officeList";
    public static final String CACHE_OFFICE_ALL_LIST =  Constant.APP_CACHE+"officeAllList";
    public static final String CACHE_DISEASE_LIST =  Constant.APP_CACHE+"diseaseList"; //用户拥有的配置项
    public static final String CACHE_IMAGE_COMBINATION_DISEASE_NAME =  Constant.APP_CACHE+"imageDiseaseName"; //图片-配置项组合名称
    public static final String CACHE_MENU_NAME_PATH_MAP = Constant.APP_CACHE+"menuNamePathMap";

    public static final String cacheKeyPrefix = Constant.APP_CACHE+"shiro_cache_";
    public static final String cacheKeyName = Constant.APP_CACHE+"reids-cache";



    /*Disease*/
    public static final String CACHE_ORGAN =  Constant.APP_CACHE+"organ";

    /**
     * 用户类型  1：系统管理员 ，2：医生，3：主任 ，4：专家，5：顾问
     * */
     public static final String MANAGER =  "1";
     public static final String DOCTOR =  "2";
     public static final String DIRECTOR =  "3";
     public static final String EXPERT =  "4";
     public static final String ADVISER =  "5";


    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    public static final String LABEL_ONE_UN = "unLabel10";
    public static final String LABEL_ONE_ON = "onLabel10";
    public static final String LABEL_TWO_UN = "unLabel11";
    public static final String LABEL_TWO_ON = "onLabel11";
    public static final String LABEL_EXPERT_UN = "unLabel20";
    public static final String LABEL_EXPERT_ON = "onLabel20";
    public static final String LABEL_ADVISER_UN = "unLabel30";
    public static final String LABEL_ADVISER_ON = "onLabel30";
    public static final String LABEL_ONE_SUBMIT= "submit10";
    public static final String LABEL_TWO_SUBMIT = "submit11";
    public static final String LABEL_20_SUBMIT = "submit20";
    public static final String LABEL_30_SUBMIT = "submit30";


    /**
     * 用户1：系统管理员 ，2：医生，3：主任 ，4：专家，5：顾问
     * */
    public static final String USER_ADMIN = "1";
    public static final String USER_DOCTOR = "2";
    public static final String USER_DIRECTOR= "3";
    public static final String USER_EXPERT= "4";
    public static final String USER_ADVISOR= "5";

}
