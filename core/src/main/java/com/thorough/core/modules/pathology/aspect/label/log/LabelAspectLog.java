package com.thorough.core.modules.pathology.aspect.label.log;


import com.alibaba.fastjson.JSONObject;
import com.thorough.core.modules.pathology.model.entity.Label;
import com.thorough.core.modules.pathology.model.vo.LabelForm;
import com.thorough.core.modules.pathology.model.vo.LabelsForm;
import com.thorough.core.modules.pathology.service.LabelService;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
@Aspect
public class LabelAspectLog implements Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(LabelAspectLog.class);
    @Autowired
    LabelService labelService;

    public LabelAspectLog(){
        super();
    }

    /*
    * 方法 方法保存之前取id，方法保存之后取id
    * */
    @Around(value = "@annotation(labelLog)")
    public Object log(final ProceedingJoinPoint pjp, final LabelLog labelLog) throws Throwable {
        Object result;
        String operate = labelLog.value();
        if (StringUtils.isNotBlank(operate)){
            if ("save".equals(operate)){
               result = pjp.proceed();
               try {
                   //保存单条记录之后
                   ResponseEntity response = (ResponseEntity) result;
                   ResponseWrapper body = (ResponseWrapper) response.getBody();
                   if (body.isSuccess()){
                       HashMap data = (HashMap) body.getData();
                       String id = (String)data.get("id");
                       if (StringUtils.isNotBlank(id)){
                           List<String> idList = new ArrayList<>();
                           idList.add(id);
                           //保存
                           label2Log(idList,operate,"after",new Date());
                       }
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
            }else if ("saveMultiManual".equals(operate)){
                result = pjp.proceed();
                //手动保存之后
                try {
                    ResponseEntity response = (ResponseEntity) result;
                    ResponseWrapper body = (ResponseWrapper) response.getBody();
                    if (body.isSuccess()){
                        HashMap data = (HashMap) body.getData();
                        List<String> success = (List<String>) data.get("ids");
                        //保存
                        label2Log(success,operate,"after",new Date());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if ("multiSaveAuto".equals(operate)){
                result = pjp.proceed();
                try {
                    //自动标注保存之后
                    ResponseEntity response = (ResponseEntity) result;
                    ResponseWrapper body = (ResponseWrapper) response.getBody();
                    if (body.isSuccess()){
                        HashMap data = (HashMap) body.getData();
                        List<String> success = (List<String>) data.get("stampIds");
                        //保存
                        label2Log(success,operate,"after",new Date());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if ("updateMultiLabel".equals(operate)){
                try {
                    //更新之前
                    Object[] args = pjp.getArgs();
                    if (args !=null && args.length > 0){
                        LabelsForm labelsForm = (LabelsForm) args[0];
                        List<LabelForm> labelList = labelsForm.getLabels();
                        List<String> ids = new ArrayList<>();
                        for(LabelForm labelForm:labelList){
                            ids.add(labelForm.getId());
                        }
                        //保存
                        label2Log(ids,operate,"before",new Date());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                result = pjp.proceed();
                try {
                    //更新之后 youhua异步
                    ResponseEntity response = (ResponseEntity) result;
                    ResponseWrapper body = (ResponseWrapper) response.getBody();
                    if (body.isSuccess()){
                        HashMap data = (HashMap) body.getData();
                        List<String> success = (List<String>) data.get("success");
                        //保存
                        label2Log(success,operate,"after",new Date());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if ("delete".equals(operate)){
                //删除之前
                try {
                    Object[] objects = pjp.getArgs();
                    if (objects != null && objects.length > 0){
                        Label label = (Label) objects[0];
                        String id = label.getId();
                        String delFlag = label.getDelFlag();
                        List list = new ArrayList();
                        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(delFlag)){
                            if ("1".equals(delFlag)){
                                Label deleteLabel = labelService.selectByPrimaryKey(id);
                                if (deleteLabel != null){
                                    list.add(id);
                                }
                            }
                        }else if (StringUtils.isNotBlank(id)){
                            Label deleteLabel = labelService.selectByPrimaryKey(id);
                            if (deleteLabel != null){
                                list.add(id);
                            }
                        }
                        //保存
                        label2Log(list,operate,"before",new Date());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //正式删除
                result = pjp.proceed();
            }else if ("batchDelete".equals(operate)){
                try {
                    Object[] objects = pjp.getArgs();
                    if (objects != null && objects.length > 0){
                        LabelsForm labelsForm = (LabelsForm) objects[0];
                        List<LabelForm> labelForms = labelsForm.getLabels();
                        if (labelForms !=null && labelForms.size() > 0){
                            List list = new ArrayList();
                            for (LabelForm labelForm:labelForms){
                                if (StringUtils.isNotBlank(labelForm.getId())){
                                    list.add(labelForm.getId());
                                }
                            }
                            //保存
                            label2Log(list,operate,"before",new Date());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //正式删除
                result = pjp.proceed();
            }
            else {
                result = pjp.proceed();
            }

        }else
            result = pjp.proceed();
        return result;
    }

    public void label2Log(List<String> ids, String operate, String type, Date date){
        User user = UserUtils.getUser();
        if (ids != null && ids.size() > 0){
            String timefile = DateUtils.formatDate(new Date());
            String path = PropertyUtil.getProperty("logging.backup")+"/backuplog-"+timefile+".json";
            File file = new File(path);
            if (!file.exists()){
                FileUtils.createFile(path);
            }
            int lenght = 90;
            String time = null;
            if (date!=null)
                time = DateUtils.formatDateTime(date);
            else
                time = DateUtils.formatDateTime(new Date());
            List<String> lines = new ArrayList<>();
            for (String id:ids){
                Label label = labelService.selectByPrimaryKey(id);
                if (label !=null){

                    String labelString = JSONObject.toJSONString(label);
                    String message = "["+time+"  "+user.getId()+"  "+ user.getName()+"  "+operate+"  "+type+"]";
                    if (message.length() >= 90)
                        lenght = message.length();
                    String head = String.format(message+"%1$"+(lenght-message.length())+"s", " ");
                    lines.add(head+labelString);
                }
            }

            FileUtils.writeLines(path,lines,true);
        }
    }


    /** {@inheritDoc} */
    public int getOrder() {
        return 999;
    }
}
