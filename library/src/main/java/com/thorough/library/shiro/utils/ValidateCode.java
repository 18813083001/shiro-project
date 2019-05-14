package com.thorough.library.shiro.utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidateCode {

    /**
     * 是否是验证码登录
     *
     * @param useruame 用户名
     * @param isFail   计数加1
     * @param clean    计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
        if (useruame == null)
            useruame = "";
        Map<String, Integer> loginFailMap = (Map<String, Integer>) ShiroCacheUtils.get("loginFailMap");
        if (loginFailMap == null) {
            loginFailMap = new ConcurrentHashMap<>();
            ShiroCacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum == null) {
            loginFailNum = 0;
        }
        if (isFail) {
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean) {
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 0;
    }
}
