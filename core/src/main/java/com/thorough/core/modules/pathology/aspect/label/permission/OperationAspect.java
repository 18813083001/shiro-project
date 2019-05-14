package com.thorough.core.modules.pathology.aspect.label.permission;

import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.model.entity.Label;
import com.thorough.core.modules.pathology.model.vo.LabelForm;
import com.thorough.core.modules.pathology.model.vo.LabelsForm;
import com.thorough.core.modules.pathology.service.ImageUserService;
import com.thorough.core.modules.pathology.service.LabelRelativeService;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
public class OperationAspect implements Ordered {
    @Autowired
    ImageUserService imageUserService;
    @Autowired
    LabelRelativeService labelRelativeService;

    @Around(value = "@annotation(operation)")
    public Object permission(final ProceedingJoinPoint pjp, final Operation operation) throws Throwable {
        Object result;
        Object[] args = pjp.getArgs();
        String operate = operation.value();
        if (StringUtils.isNotBlank(operate)){
            if (args !=null && args.length > 0){
                try {
                    if ("save".equals(operate)){
                        String param = (String) args[0];
                        JSONObject jsonObject = new JSONObject(param);
                        String imageId = jsonObject.getString("imageId");
                        checkOwnerShip(imageId);
                    }else if ("saveMultiManual".equals(operate)){
                        JSONArray jsonArray = new JSONArray(args);
                        if (jsonArray != null && jsonArray.length() >0){
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                            String imageId = jsonObject.getString("imageId");
                            checkOwnerShip(imageId);
                        }
                    }else if ("multiSaveAuto".equals(operate)){
                        LabelsForm labelsForm = (LabelsForm) args[0];
                        String imageId = labelsForm.getImageId();
                        if (StringUtils.isBlank(imageId)) {
                            List<LabelForm> list = labelsForm.getLabels();
                            if (list != null && list.size() > 0) {
                                String id = list.get(0).getId();
                                imageId = labelRelativeService.getImageId(id);
                            }
                        }
                        checkOwnerShip(imageId);
                    }else if ("updateMultiLabel".equals(operate)){
                        LabelsForm labelsForm = (LabelsForm) args[0];
                        List<LabelForm> list = labelsForm.getLabels();
                        if (list != null && list.size() > 0){
                            String id = list.get(0).getId();
                            String imageId = labelRelativeService.getImageId(id);
                            checkOwnerShip(imageId);
                        }
                    }
                    else if ("delete".equals(operate)){
                        Label label = (Label) args[0];
                        String id = label.getId();
                        String imageId = labelRelativeService.getImageId(id);
                        checkOwnerShip(imageId);
                    }else if ("batchDelete".equals(operate)){
                        LabelsForm labelsForm = (LabelsForm) args[0];
                        List<LabelForm> list = labelsForm.getLabels();
                        if (list != null && list.size() > 0){
                            String id = list.get(0).getId();
                            String imageId = labelRelativeService.getImageId(id);
                            checkOwnerShip(imageId);
                        }
                    }
                }catch (Exception e){
                    if (e instanceof CoreException){
                        throw e;
                    }
                }
            }
        }
        result = pjp.proceed();
        return result;
    }

    private void checkOwnerShip(String imageId){
        if (StringUtils.isNotBlank(imageId)){
            String userId = UserUtils.getUser().getId();
            int ownerShip = imageUserService.getOwnership(imageId,userId);
            if (ownerShip == 0)
                throw new CoreException("没有切片所属权");
        }else throw new CoreException("切片ID为空，无法获取图片的所属权");
    }

    @Override
    public int getOrder() {
        return 998;
    }

//    @Pointcut("execution(* com.thorough.modules.pathology.web.LabelController.*(..))")
//    public void pointcutName(){}
//    @Before("pointcutName()")
//    public void performance(){
//        System.out.println("Spring AOP");
//    }
}
