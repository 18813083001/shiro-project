package com.thorough.core.modules.pathology.aspect.image;

import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.service.ImageService;
import com.thorough.core.modules.pathology.service.LabelRelativeService;
import com.thorough.core.modules.pathology.service.LabelService;
import com.thorough.library.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;


public class LabelStatusAspect implements Ordered {

    private int order = -1;
    @Autowired
    LabelService labelService;
    @Autowired
    LabelRelativeService labelRelativeService;
    @Autowired
    ImageService imageService;
    /*
   * 方法 方法保存之前取id，方法保存之后取id
   * */
    @Around(value = "@annotation(labelStatus)", argNames = "labelStatus")
    public Object backUpLabel(final ProceedingJoinPoint pjp, final LabelStatus labelStatus) throws Throwable {
        Object result = pjp.proceed();
//        try {
//            String operate = labelStatus.value();
//            if (StringUtils.isNotBlank(operate)){
//                //解析参数，获取imageId
//                if ("save".equals(operate)){
//                    ResponseEntity response = (ResponseEntity) result;
//                    ResponseWrapper body = (ResponseWrapper) response.getBody();
//                    if (body.isSuccess()){
//                        HashMap data = (HashMap) body.getData();
//                        String id = (String)data.get("id");
//                        if (StringUtils.isNotBlank(id)){
//                           String imageId = labelRelativeService.getImageId(id);
//                           updateImage(imageId,1);
//                        }
//                    }
//
//                }else if ("saveMultiManual".equals(operate)){
//                    ResponseEntity response = (ResponseEntity) result;
//                    ResponseWrapper body = (ResponseWrapper) response.getBody();
//                    if (body.isSuccess()){
//                        HashMap data = (HashMap) body.getData();
//                        List<String> ids = (List<String>) data.get("ids");
//                        if (ids != null && ids.size() > 0){
//                            String imageId = labelRelativeService.getImageId(ids.get(0));
//                            updateImage(imageId,1);
//                        }
//                    }
//
//                }else if ("multiSaveAuto".equals(operate)){
//                    ResponseEntity response = (ResponseEntity) result;
//                    ResponseWrapper body = (ResponseWrapper) response.getBody();
//                    if (body.isSuccess()){
//                        HashMap data = (HashMap) body.getData();
//                        List<String> stampIds = (List<String>) data.get("stampIds");
//                        if (stampIds != null && stampIds.size() > 0){
//                            String imageId = labelRelativeService.getImageId(stampIds.get(0));
//                            updateImage(imageId,1);
//                        }
//                    }
//
//
//                }else if ("updateMultiLabel".equals(operate)){
//
//                }
//                else if ("delete".equals(operate)){
//                    //删除之前
//                    try {
//                        Object[] objects = pjp.getArgs();
//                        if (objects != null && objects.length > 0){
//                            Label label = (Label) objects[0];
//                            String id = label.getId();
//
//
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    //正式删除
//                }else if ("batchDelete".equals(operate)){
//                    try {
//                        Object[] objects = pjp.getArgs();
//                        if (objects != null && objects.length > 0){
//                            List<LabelForm> labelForms = (List<LabelForm>) objects[0];
//
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return result;
    }

    private void updateImage(String imageId,int labelStatus){
        if (StringUtils.isNotBlank(imageId)){
            Image image = new Image();
            image.setLabelStatus(labelStatus);
            imageService.updateByPrimaryKeySelective(image);
        }
    }

    /** {@inheritDoc} */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order
     *            the order to set
     */
    public void setOrder(final int order) {
        this.order = 998;
    }
}
