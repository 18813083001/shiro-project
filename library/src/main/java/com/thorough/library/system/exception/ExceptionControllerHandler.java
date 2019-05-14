//package com.thorough.library.system.exception;
//
//import com.thorough.library.utils.ExceptionUtils;
//import com.thorough.library.utils.ResponseWrapper;
//import com.thorough.library.utils.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Enumeration;
//
//@ControllerAdvice
//public class ExceptionControllerHandler {
//    private Logger logger = LoggerFactory.getLogger(ExceptionControllerHandler.class);
//
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ResponseEntity<?> handleControllerException(HttpServletRequest request, Exception exception) {
//        ResponseWrapper expWrapper = new ResponseWrapper();
//        Enumeration<String> parameter = request.getParameterNames();
//        String paramNameValue = "";
//        if (parameter != null){
//            while (parameter.hasMoreElements()){
//                String name = parameter.nextElement();
//                String[] valueArry = request.getParameterValues(name);
//                String values = "";
//                if (valueArry != null && valueArry .length > 0){
//                    for(int i = 0; i < valueArry.length;){
//                        values += valueArry[i];
//                        i++;
//                        if (i < valueArry.length){
//                            values += ",";
//                        }
//                    }
//                }
//                paramNameValue += name+"="+values;
//                if (parameter.hasMoreElements()){
//                    paramNameValue +="&";
//                }
//            }
//        }
//        if (StringUtils.isNotBlank(paramNameValue)){
//            logger.error("Request url is:"+request.getRequestURI()+"?"+paramNameValue);
//        }else {
//            logger.error("Request url is:"+request.getRequestURI());
//        }
//        logger.error("Server Error:", ExceptionUtils.getStackTraceAsString(exception));
//
//        expWrapper.setMessage("Server Error！");
//        expWrapper.setSuccess(false);
//        return new ResponseEntity<Object>(expWrapper, HttpStatus.OK);
//    }
//}
//
