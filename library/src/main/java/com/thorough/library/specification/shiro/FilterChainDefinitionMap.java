package com.thorough.library.specification.shiro;


import javax.servlet.Filter;
import java.util.Map;

public interface FilterChainDefinitionMap {
    Map<String,String> getFilterMap();
    Map<String,Filter> getFilter();
}
