package com.thorough.core.modules.pathology.web;


import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.imglib.CacheImage;
import com.thorough.core.modules.pathology.imglib.openslide.CacheOpenSlideImage;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.vo.ImageInputVo;
import com.thorough.core.modules.pathology.model.vo.ImageVo;
import com.thorough.core.modules.pathology.model.vo.InputImageVo;
import com.thorough.core.modules.pathology.service.ImageService;
import com.thorough.core.modules.pathology.service.ImageUserService;
import com.thorough.core.modules.pathology.util.CacheImageHolder;
import com.thorough.core.modules.pathology.util.ReviewStatusUtils;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.system.model.entity.Office;
import com.thorough.library.system.service.OfficeService;
import com.thorough.library.utils.FileUtils;
import com.thorough.library.utils.PropertyUtil;
import com.thorough.library.utils.ResponseBuilder;
import com.thorough.library.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scala.Int;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping(value = "${adminPath}/pathology/imageInput/")
public class ImageInputController extends BaseController{

    @Autowired
    OfficeService officeService;
    @Autowired
    CoreDiseaseService coreDiseaseService;
    @Autowired
    ImageService imageService;
    @Autowired
    ImageUserService imageUserService;
    @Autowired
    CacheImageHolder cacheImageHolder;
    @Autowired
    ImageController imageController;

    Map<String,ImageVo> imageMap = new ConcurrentHashMap<>();

    private static final int initIndex = 1;
    ThreadLocal<Integer> threadLocal = new ThreadLocal(){
        @Override
        protected Object initialValue() {
            return initIndex;
        }
    };

    @RequestMapping(value = "imageInputStatus")
    public ResponseEntity<?> getImageInputStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map<String, String>> inputStatus = ReviewStatusUtils.getImageInputStatus();
        builder.add("statusList",inputStatus);
        return builder.build();
    }

    @RequestMapping(value = "imagePreview")
    @ResponseBody
    public ResponseEntity<?> imagePreview(InputImageVo inputImageVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        imageMap.clear();
        try {
            //生成预览信息
            List<Image> sourceList = imageInput(inputImageVo);
            //生成预览信息
            List<ImageVo> targetList = new ArrayList<>();
            if (sourceList != null && sourceList.size() > 0){
                for (Image image: sourceList){
                    ImageVo imageVo = new ImageVo();
                    BeanUtils.copyProperties(image,imageVo);
                    imageVo.setId(image.getId());
                    imageVo.setIhc(image.getIhc());
                    String spinnerId = image.getSpinnerId();
                    if (StringUtils.isNotBlank(spinnerId)){
                        Map<String,String> map = ReviewStatusUtils.getReviewStageMapBySpinnerId(spinnerId,ReviewStatusUtils.getImageInputStatus());
                        imageVo.setStatusName(map.get("name"));
                    }
                    targetList.add(imageVo);
                    imageMap.put(imageVo.getId(),imageVo);
                }
            }
            builder.add("imageList",targetList);
        }catch (Exception e){
            e.printStackTrace();
            builder.error();
            builder.message(e.getMessage());
        }
        return builder.build();
    }


    @RequestMapping(value = "imageInput", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> imageInput(@RequestBody ImageInputVo imageInputVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<ImageVo> inputImageList = imageInputVo.getInputImageList();
        if (inputImageList == null || inputImageList.size() == 0){
            builder.error();
            builder.message("没有上传切片信息");
            return builder.build();
        }
        if (imageInputVo.getAllocation() == null){
            builder.error();
            builder.message("allocation不能未空");
            return builder.build();
        }else if (!(imageInputVo.getAllocation() ==0 || imageInputVo.getAllocation() == 2)){
            builder.error();
            builder.message("allocation只能为0或者2");
            return builder.build();
        }

        imageMap.clear();
        List<Image> imageList = new ArrayList<>();
        for (int i=0;i < inputImageList.size();i++){
            ImageVo imageVo = inputImageList.get(i);
            Image image = new Image();
            BeanUtils.copyProperties(imageVo,image);
            image.setId(imageVo.getId());
            List<Map<String,String>> reviewStageMapList = ReviewStatusUtils.getImageInputStatus();
            Map<String,String> reviewStageMap = ReviewStatusUtils.getReviewStageMapBySpinnerId(imageVo.getSpinnerId(),reviewStageMapList);
            String reviewStage = reviewStageMap.get("reviewStage");
            String labelStatus = reviewStageMap.get("labelStatus");
            image.setReviewStage(Integer.parseInt(reviewStage));
            image.setLabelStatus(Integer.parseInt(labelStatus));
            image.setPatientId("");
            image.setPatientName("");
            image.setNewRecord(true);
            image.setAllocation(imageInputVo.getAllocation());
            imageList.add(image);
        }
        imageService.insertatch(imageList);
        builder.message("成功录入"+inputImageList.size()+"条数据");
        return builder.build();
    }


    private List<Image> imageInput(InputImageVo inputImageVo) throws IOException {
        //HE片子相关的信息
        Map<String, Object> heInfoMap = getInputMessageInfo(inputImageVo);
        String maxImageId = (String) heInfoMap.get("maxImageId");
        String hospitalId = (String) heInfoMap.get("hospitalId");
        String hospitalName = (String) heInfoMap.get("hospitalName");
        String departmentId = (String) heInfoMap.get("departmentId");
        String departmentName = (String) heInfoMap.get("departmentName");
        String diseaseId = (String) heInfoMap.get("diseaseId");
        String diseaseNameCombination = (String) heInfoMap.get("diseaseNameCombination");
        String remarks = (String) heInfoMap.get("remarks");
        String spinnerId = (String) heInfoMap.get("spinnerId");
        String path = (String) heInfoMap.get("path");
        String imageNamePrefix = (String) heInfoMap.get("imageNamePrefix");
        int count = Integer.parseInt((String) heInfoMap.get("count"));
        List<String> nameList = (List<String>) heInfoMap.get("imageNameList");

        //免疫组化片子相关的信息
        String ihcHospitalId = null;
        String ihcHospitalName = null;
        String ihcDepartmentId = null;
        String ihcDepartmentName = null;
        String ihcDiseaseId = null;
        String ihcDiseaseNameCombination = null;
        String ihcRemarks = null;
        String ihcSpinnerId = null;
        String ihcPath = null;
        List<String> iHcNameList = null;
        if (inputImageVo.getIhc() == 1){
            inputImageVo.setHospitalId(inputImageVo.getHospitalIdIhc());
            inputImageVo.setSpinnerId(inputImageVo.getSpinnerIdIhc());
            inputImageVo.setDiseaseId(inputImageVo.getDiseaseIdIhc());
            inputImageVo.setPath(inputImageVo.getPathIhc());
            inputImageVo.setRemarks(inputImageVo.getRemarksIhc());
            inputImageVo.setHeFile(inputImageVo.getFileIhc());
            Map<String, Object> ihcInfoMap = getInputMessageInfo(inputImageVo);

            ihcHospitalId = (String) ihcInfoMap.get("hospitalId");
            ihcHospitalName = (String) ihcInfoMap.get("hospitalName");
            ihcDepartmentId = (String) ihcInfoMap.get("departmentId");
            ihcDepartmentName = (String) ihcInfoMap.get("departmentName");
            ihcDiseaseId = (String) ihcInfoMap.get("diseaseId");
            ihcDiseaseNameCombination = (String) ihcInfoMap.get("diseaseNameCombination");
            ihcRemarks = (String) ihcInfoMap.get("remarks");
            ihcSpinnerId = (String) ihcInfoMap.get("spinnerId");
            ihcPath = (String) ihcInfoMap.get("path");
            iHcNameList = (List<String>) ihcInfoMap.get("imageNameList");
        }
        //初始化，防止线程池重用threadLocal的值,两个方法之间用threadLocal传递参数
        threadLocal.set(1);
        int index = threadLocal.get();
        String[] abc = {"A","B","C","D","E","F","G","H","I","J","K"};
        List<Image> imageList = new ArrayList<>();
        int id = Integer.parseInt(maxImageId);
        for (String fileName:nameList){
            if (StringUtils.isNotBlank(fileName)){
                id = id + 1;
                Image image = getFillImage(id+"", hospitalId, hospitalName, departmentId, departmentName,
                        diseaseId, diseaseNameCombination, remarks, spinnerId, path, fileName, count, imageNamePrefix);
                image.setIhc(0);
                imageList.add(image);
                //添加免疫组化
                if (iHcNameList != null && iHcNameList.size() > 0){
                    List<String> ihcChildNameList = getImageNameIhc(iHcNameList,fileName);
                    if (ihcChildNameList != null){
                        if (ihcChildNameList.size() > abc.length)
                            throw new CoreException("免疫组化的片子太多，系统最多支持一张HE片子对应11张免疫组化片子");
                        if (ihcChildNameList.size() > 0){
                            for (int k = 0; k < ihcChildNameList.size();k++){
                                String ihcFileName = ihcChildNameList.get(k);
                                id = id + 1;
                                String imageName = image.getName()+abc[k];
                                Image imageIhc = getFillImageIhc(id+"", ihcHospitalId, ihcHospitalName, ihcDepartmentId, ihcDepartmentName,
                                        ihcDiseaseId, ihcDiseaseNameCombination, ihcRemarks, ihcSpinnerId, ihcPath, ihcFileName, imageName);
                                image.setIhc(1);
                                imageList.add(imageIhc);
                            }
                        }
                    }
                }
                index = threadLocal.get();
                index++;
                threadLocal.set(index);
            }
        }
        threadLocal.remove();
        return imageList;
    }

    public Image getFillImage(String id,
                               String hospitalId,
                               String hospitalName,
                               String departmentId,
                               String departmentName,
                               String diseaseId,
                               String diseaseNameCombination,
                               String remarks,
                               String spinnerId,
                               String path,
                               String fileName,
                               long count,
                               String imageNamePrefix){
        Image image = new Image();
        image.setId(id);
        image.setHospitalId(hospitalId);
        image.setHospitalName(hospitalName);
        image.setDepartmentId(departmentId);
        image.setDepartmentName(departmentName);
        image.setDiseaseId(diseaseId);
        image.setDiseaseName(diseaseNameCombination);
        image.setRemarks(remarks);
        image.setSpinnerId(spinnerId);
        image.setPath(path+fileName);
        image.setPatientId("");
        image.setPatientName("");

        //生成名字，数值部分的编号长度是5个
        String number = String.format("%05d", count+threadLocal.get());//imageNamePrefix = organCode+dyingCode+regionCode; count是统计前缀为imageNamePrefix的片子有多少
        String name = imageNamePrefix+"-"+number;
        //以防image表中的数据被删除，导致count小于实际的数据
        while (true){
            CommonExample imageExample = new CommonExample(Image.class);
            imageExample.createCriteria().andEqualTo(Image.getFieldName(),name);
            long sameNameCount = imageService.countByExample(imageExample);
            if (sameNameCount == 0)
                break;
            else {
                int index = threadLocal.get();
                index++;
                threadLocal.set(index);
                number = String.format("%05d", count+index);
                name = imageNamePrefix+"-"+number;
            }
        }
        image.setName(name);
        return image;
    }

    public Image getFillImageIhc(String id,
                              String hospitalId,
                              String hospitalName,
                              String departmentId,
                              String departmentName,
                              String diseaseId,
                              String diseaseNameCombination,
                              String remarks,
                              String ihcSpinnerId,
                              String path,
                              String fileName,
                              String imageName){
        Image image = new Image();
        image.setId(id);
        image.setHospitalId(hospitalId);
        image.setHospitalName(hospitalName);
        image.setDepartmentId(departmentId);
        image.setDepartmentName(departmentName);
        image.setDiseaseId(diseaseId);
        image.setDiseaseName(diseaseNameCombination);
        image.setRemarks(remarks);
        image.setSpinnerId(ihcSpinnerId);
        image.setPath(path+fileName);
        image.setPatientId("");
        image.setPatientName("");
        image.setName(imageName);
        return image;
    }

    public List<String> getImageNameIhc(List<String> ihcNameList,String heName){
        heName = heName.substring(0,heName.lastIndexOf("."));
        if (ihcNameList != null && ihcNameList.size() > 0 && StringUtils.isNotBlank(heName)){
            List<String> childList = new ArrayList<>();
            for (String imageName : ihcNameList){
                if (imageName.startsWith(heName))
                    childList.add(imageName);
            }
            return childList;
        }else
            return null;
    }

    /**
     * 录入包含免疫组化的片子
     * */
    private Map<String, Object> getInputMessageInfo(InputImageVo inputImageVo) throws IOException {
        Map<String,Object> inputImageInfo = new HashMap<>();
        //文件
        File temp = File.createTempFile("imageNameFile","txt");
        MultipartFile multipartFile = inputImageVo.getHeFile();
        multipartFile.transferTo(temp);
        List<String> imageNameList =  FileUtils.readLines(temp,"utf-8");
        temp.delete();
        //路径
        String path = inputImageVo.getPath();
        if (!path.endsWith("/"))
            path +="/";
        //医院
        String hospitalId = inputImageVo.getHospitalId();
        Office office = officeService.get(hospitalId);
        String hospitalName = office.getName();
        //科室
        List<String> departmentIdList = officeService.getChildIdByParentId(hospitalId);
        String departmentName = "";
        String departmentId = "";
        if (departmentIdList != null && departmentIdList.size() > 0){
            office = officeService.get(departmentIdList.get(0));
            departmentName = office.getName();
            departmentId = office.getId();
        }
        //录入状态ID
        String spinnerId = inputImageVo.getSpinnerId();
//        List<Map<String,String>> reviewStageMapList = ReviewStatusUtils.getImageInputStatus();
//        Map<String,String> reviewStageMap = ReviewStatusUtils.getReviewStageMapBySpinnerId(spinnerId,reviewStageMapList);
//        String reviewStage = reviewStageMap.get("reviewStage");
        //配置项
        String regionId = inputImageVo.getDiseaseId();//分类ID
        Disease disease = coreDiseaseService.selectByPrimaryKey(regionId);
        if (!Constant.CATEGORY_RIGION.equals(disease.getCategory()))
            throw new CoreException("diseaseId必须为分类ID");
        List<String> diseaseIdList = coreDiseaseService.getChildsByParentIdAndCategory(regionId, Constant.CATEGORY_DISEASE);
        String diseaseId;
        if (diseaseIdList == null || diseaseIdList.size() == 0)
            throw new CoreException("该分类没有具体疾病，请先在分类下添加疾病信息");
        else {
            diseaseId = diseaseIdList.get(0);
        }

        //备注
        String remarks = inputImageVo.getRemarks();
        String imageName ;
        Map<String,String> region = coreDiseaseService.getParentByChildId(diseaseId, Constant.CATEGORY_RIGION);
        Map<String,String> dying = coreDiseaseService.getParentByChildId(diseaseId, Constant.CATEGORY_DYEING);
        Map<String,String> organ = coreDiseaseService.getParentByChildId(diseaseId, Constant.CATEGORY_ORGAN);
        String regionCode = region.get("code").substring(0,2).toUpperCase();
        String dyingCode = dying.get("code").substring(0,2).toUpperCase();
        String organCode = organ.get("code").substring(0,2).toUpperCase();
        String regionName = region.get("name");
        String dyingName = dying.get("name");
        String organName = organ.get("name");
        //判断是否有重复的code(前两位首字母)
        CommonExample diseaseExample = new CommonExample(Disease.class);
        diseaseExample.createCriteria().
                andLike(Disease.getFieldCode(),organCode+"%").
                andEqualTo(Disease.getFieldCategory(),Constant.CATEGORY_ORGAN).
                andEqualTo(Disease.getFieldDelFlag(),"0");
        long count = coreDiseaseService.countByExample(diseaseExample);
        if (count > 1){
            //如果有重复的首字母，取前三位首字母
            organCode = dying.get("code").substring(0,3);
            diseaseExample = new CommonExample(Disease.class);
            diseaseExample.createCriteria().
                    andLike(Disease.getFieldCode(),organCode+"%").
                    andEqualTo(Disease.getFieldCategory(),Constant.CATEGORY_ORGAN).
                    andEqualTo(Disease.getFieldDelFlag(),"0");
            count = coreDiseaseService.countByExample(diseaseExample);
            //如果有重复的首字母，取前四位首字母
            if (count > 1)
                organCode = dying.get("organ").substring(0,4);
        }
        //imageName 等于 器官首字母 染色首字母 分类首字母 + 编号
        imageName = organCode+dyingCode+regionCode;
        //查看当前imageName是否已经存在编号，有就继续往后排，没有则从1开始排
        CommonExample imageExample = new CommonExample(Image.class);
        imageExample.createCriteria().andLike(Image.getFieldName(),imageName+"%");
        count = imageService.countByExample(imageExample);
        int maxImageId = imageUserService.getMaxImageId();

        String diseaseNameCombination = organName+"/"+dyingName+"/"+regionName;

        inputImageInfo.put("path",path);
        inputImageInfo.put("regionId",inputImageVo.getDiseaseId());
        inputImageInfo.put("diseaseId",diseaseId);
        inputImageInfo.put("hospitalId",hospitalId);
        inputImageInfo.put("hospitalName",hospitalName);
        inputImageInfo.put("departmentId",departmentId);
        inputImageInfo.put("departmentName",departmentName);
        inputImageInfo.put("spinnerId",spinnerId);
        inputImageInfo.put("remarks",remarks);
        inputImageInfo.put("maxImageId",maxImageId+"");
        inputImageInfo.put("imageNamePrefix",imageName);
        inputImageInfo.put("count",count+"");
        inputImageInfo.put("diseaseNameCombination",diseaseNameCombination);
        inputImageInfo.put("imageNameList",imageNameList);
        return inputImageInfo;
    }

    @RequestMapping("/tile")
    public void tile(HttpServletResponse response, @RequestParam String imageId) throws Exception {
        int level = 8;
        int col = 0;
        int row = 0;
        ImageVo imageVo = imageMap.get(imageId);
        CacheOpenSlideImage cacheImage = (CacheOpenSlideImage) cacheImageHolder.loadCacheImageByPathName(imageVo.getPath(),imageVo.getName());
        BufferedImage img = cacheImage.getTile(level, Arrays.asList(col, row));
        //将图片输出给浏览器
        response.setContentType("image/jpeg");
        imageController.outputImage(response, img, cacheImage);
        cacheImage.close();

    }


    @RequestMapping("getLabelInfo")
    public void getLabelInfo(HttpServletResponse response, @RequestParam String imageId) throws Exception {
        ImageVo imageVo = imageMap.get(imageId);
        CacheOpenSlideImage cacheImage = (CacheOpenSlideImage) cacheImageHolder.loadCacheImageByPathName(imageVo.getPath(),imageVo.getName());
        BufferedImage img = cacheImage.getLabelInfo(cacheImage.getPath());
        ImageIO.write(img, "png", response.getOutputStream());
        cacheImage.close();
    }
}
