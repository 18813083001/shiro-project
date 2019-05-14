package com.thorough.library.system.exception;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.thorough.library.utils.ExceptionUtils;
import com.thorough.library.utils.JsonMapper;
import com.thorough.library.utils.ResponseWrapper;
import com.thorough.library.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

@Component
public class GlobalHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {

    private Logger logger = LoggerFactory.getLogger(GlobalHandlerExceptionResolver.class);
    private int order = Ordered.HIGHEST_PRECEDENCE;
    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ResponseWrapper expWrapper = new ResponseWrapper();
        Enumeration<String> parameter = request.getParameterNames();
        String paramNameValue = "";
        if (parameter != null){
            while (parameter.hasMoreElements()){
                String name = parameter.nextElement();
                String[] valueArray = request.getParameterValues(name);
                String values = "";
                if (valueArray != null && valueArray .length > 0){
                    for(int i = 0; i < valueArray.length;){
                        values += valueArray[i];
                        i++;
                        if (i < valueArray.length){
                            values += ",";
                        }
                    }
                }
                paramNameValue += name+"="+values;
                if (parameter.hasMoreElements()){
                    paramNameValue +="&";
                }
            }
        }
        if (StringUtils.isNotBlank(paramNameValue)){
            logger.error("Request url is:"+request.getRequestURI()+"?"+paramNameValue);
        }else {
            logger.error("Request url is:"+request.getRequestURI());
        }
        String stackTrace = ExceptionUtils.getStackTraceAsString(ex);
        logger.error("Server Error: "+stackTrace);

        if (ex instanceof MissingServletRequestParameterException || ex instanceof IllegalArgumentException)
            expWrapper.setMessage("参数错误！");
        else if (ex instanceof NullPointerException || ex instanceof LibraryException || ex instanceof RuntimeException || ex instanceof MySQLIntegrityConstraintViolationException)
            expWrapper.setMessage("服务器错误！");
        else
            expWrapper.setMessage("未知错误！");
        expWrapper.setSuccess(false);
        expWrapper.add("stackTrace",ex.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        if (!response.isCommitted()){
            try {
                response.getWriter().print(JsonMapper.toJsonString(expWrapper));
            } catch (IOException e) {
                throw new LibraryException(e);
            }
        }
        return new ModelAndView();
    }
}
