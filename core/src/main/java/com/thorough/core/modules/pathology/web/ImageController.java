package com.thorough.core.modules.pathology.web;

import com.sun.org.apache.regexp.internal.RE;
import com.thorough.core.modules.cache.MemoryCache;
import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.imglib.CacheImage;
import com.thorough.core.modules.pathology.imglib.enums.ImageType;
import com.thorough.core.modules.pathology.imglib.kfb.KfbImage;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.vo.*;
import com.thorough.core.modules.pathology.service.DiseaseCustomService;
import com.thorough.core.modules.pathology.service.ImageService;
import com.thorough.core.modules.pathology.service.ImageUserService;
import com.thorough.core.modules.pathology.service.ReviewPoolImageService;
import com.thorough.core.modules.pathology.util.CacheImageHolder;
import com.thorough.core.modules.pathology.util.ReviewStatusUtils;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.constant.Constant;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.redis.utils.RedisUtils;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.system.model.entity.Office;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.service.OfficeService;
import com.thorough.library.system.utils.CacheUtils;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.*;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scala.Int;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Controller
@RequestMapping(value = "${adminPath}/pathology/image/")
public class ImageController extends BaseController {

    @Autowired
    ImageService imageService;
    @Autowired
    CacheImageHolder cacheImageHolder;
    @Autowired
    ImageUserService imageUserService;
    @Autowired
    OfficeService officeService;
    @Autowired
    CoreDiseaseService coreDiseaseService;
    @Autowired
    DiseaseCustomService diseaseCustomService;
    @Autowired
    ReviewPoolImageService reviewPoolImageService;

    @RequestMapping(value = "/image")
    public ResponseEntity<?> image(@RequestParam String id) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Image image = this.imageService.selectByPrimaryKey(id);
        builder.add("image", image);
        return builder.build();
    }

    @RequestMapping(value = "delete")
    public String delete(Image image, RedirectAttributes redirectAttributes) {
        imageService.deleteByPrimaryKey(image.getId());
        addMessage(redirectAttributes, "删除切片成功");
        return "redirect:" + adminPath + "/modules/pathology/iamgeList?id=" + image.getId();
    }

    @RequestMapping(value = "updateDifficult")
    public ResponseEntity<?> updateDifficult(@RequestParam String id, @RequestParam(name = "diseaseRegionId", required = false) String diseaseRegionId, String describes) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        try {
            Map map = imageService.updateDifficult(id, diseaseRegionId, describes);
            builder.add(map);
            builder.message("更新成功！");
        } catch (Exception e) {
            builder.error();
            builder.message(e.getMessage());
        }
        return builder.build();
    }

    /*
  * 更新搜藏
  * */
    @RequestMapping(value = "updateFavorites")
    public ResponseEntity<?> updateFavorites(@RequestParam String imageId, @RequestParam int favorites, String describes) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        try {
            List userId = imageUserService.getUserIdByImageId(imageId);
            if (userId !=null && userId.size() > 0){
                Map map = imageService.updateFavorites(imageId, favorites, describes);
                builder.add(map);
                if (favorites == 1)
                    builder.message("收藏成功！");
                else if(favorites == 0){
                    builder.message("取消收藏成功！");
                }
            }else {
                builder.error();
                builder.message("你没有该切片");
            }

        } catch (Exception e) {
            builder.error();
            builder.message(e.getMessage());
        }
        return builder.build();
    }

    /*
   * 典型切片库
   * */
    @RequestMapping(value = "typical")
    public ResponseEntity<?> typical(HttpServletRequest request, HttpServletResponse response) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        String diseaseId = request.getParameter("diseaseId");
        Page<Image> page;
        page = imageService.typical(new Page<>(request, response),diseaseId);
        isReadable(page);
        if (page != null) {
            builder.add("list", getImageToTypicalMap(page.getList()));
            builder.add("total", page.getCount());
        } else {
            builder.add("list", new ArrayList<>());
            builder.add("total", 0);
        }
        return builder.build();
    }


    @RequestMapping(value = "updateReviewStatus")
    public ResponseEntity<?> updateReviewStatus(@RequestParam String id, @RequestParam String diseaseRegionId, Integer grade) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        try {
            Map map = imageService.updateReviewStatus(id, diseaseRegionId, grade);
            builder.add(map);
            builder.message("更新成功！");
        } catch (Exception e) {
            builder.error();
            builder.message(e.getMessage());
        }
        return builder.build();
    }

    @RequestMapping(value = "updateLabelNumber")
    public ResponseEntity<?> updateLabelNumber(@RequestParam String id, String type) {
        Image image1 = imageService.selectByPrimaryKey(id);
        Image imageNew = new Image();
        imageNew.setId(image1.getId());
        //如果是退回，状态改为正在标注
        if (StringUtils.isNotBlank(type) && "rollback".equals(type)&& image1.getLabelStatus() == 2){
            image1.setModifyNumber(1);
        }else {
            imageNew.setModifyNumber(image1.getModifyNumber() + 1);
        }
        imageNew.setLabelStatus(1);
        imageNew.preUpdate();
        imageService.updateByPrimaryKeySelective(imageNew);
        return ResponseBuilder.SUCCESS;
    }

    @RequestMapping(value = "update")
    public ResponseEntity<?> update(Image image) {
        image.preUpdate();
        imageService.updateByPrimaryKeySelective(image);
        return ResponseBuilder.SUCCESS;
    }

    @RequestMapping(value = "remove")
    public ResponseEntity<?> remove(@RequestParam String id) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (StringUtils.isNotBlank(id)) {
            int row = imageService.deleteByPrimaryKey(id);
            builder.add("row", row);
        } else {
            builder.error();
            builder.message("Id为空");
        }
        return builder.build();
    }

    @RequestMapping(value = {"list"})
    public String labelPendingList(Image image, HttpServletRequest request, HttpServletResponse response, Model model) {
        image.setLabelStatus(0);
        image.setPage(new Page<>(request, response));
        Page<Image> page = imageUserService.getImagesByUser(image);
        getUserNmae(page);
        model.addAttribute("page", page);
        return "modules/pathology/imageList";
    }

    @RequestMapping(value = {"toAudit"})
    public String alreadyLabelList(Image image, HttpServletRequest request, HttpServletResponse response, Model model) {
        image.setLabelStatus(1);
        image.setPage(new Page<>(request, response));
        Page<Image> page = imageUserService.getImagesByUser(image);
        getUserNmae(page);
        model.addAttribute("page", page);
        return "modules/pathology/toAudit";
    }

    /*
    * image.getActionType() == null 兼容pad老版本
    * */
    @RequestMapping(value = {"imageList"})
    public ResponseEntity<?> imageList(Image image, HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (("3".equals(image.getActionType() + "") && StringUtils.isNotBlank(image.getUserId()))
                || ("2".equals(image.getActionType() + ""))
                || ("1".equals(image.getActionType() + ""))
                || (image.getActionType() == null)) {
            Page<Image> page = new Page<>(request, response);
            if (StringUtils.isBlank(page.getOrderBy())){
                String orderBy;
                //列表排序: 正在标注、待标注、已提交
                orderBy = "iu.ownership desc,ig.label_status desc,iu.review_stage asc,ig.name asc";
                page.setOrderBy(orderBy);
            }else if (StringUtils.isNotBlank(page.getOrderBy())){
                page.setOrderBy("ig."+page.getOrderBy());
            }
            image.setPage(page);
            page = imageUserService.getImagesByUser(image);
            if (page != null) {
                getUserNmae(page);
                isReadable(page);
                setHospitalCode(page);
                model.addAttribute("page", page);
                builder.add("list", page.getList());
                builder.add("total", page.getCount());
            } else {
                builder.add("list", new ArrayList<>());
                builder.add("total", 0);
            }
            return builder.build();
        } else {
            builder.error();
            builder.message("actionType为1、2、3，null，type = 1或者null表示自己，actionType=2表示全部,actionType=3时，必须上传userId");
            return builder.build();
        }
    }

    @RequestMapping(value = {"statisticsLabelPage"})
    public ResponseEntity<?> statisticsLabelPage(Image image, HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (("3".equals(image.getActionType() + "") && StringUtils.isNotBlank(image.getUserId()))
                || ("2".equals(image.getActionType() + ""))
                || ("1".equals(image.getActionType() + ""))
                || (image.getActionType() == null)) {
            Map map;
            map = imageUserService.statisticsLabelPage(image);
            builder.add(map);
            return builder.build();
        } else {
            builder.error();
            builder.message("actionType为1、2、3，null，type = 1或者null表示自己，actionType=2表示全部,actionType=3时，必须上传userId");
            return builder.build();
        }
    }


    @RequestMapping(value = {"statisticsImage"})
    public ResponseEntity<?> statisticsManagePage(Image image, HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (("3".equals(image.getActionType() + "") && StringUtils.isNotBlank(image.getUserId()))
                || ("2".equals(image.getActionType() + ""))
                || ("1".equals(image.getActionType() + ""))
                || (image.getActionType() == null)) {
            Map map;
            map = imageUserService.statisticsManagePage(image);
            builder.add(map);
            return builder.build();
        } else {
            builder.error();
            builder.message("actionType为1、2、3，null，type = 1或者null表示自己，actionType=2表示全部,actionType=3时，必须上传userId");
            return builder.build();
        }
    }

    @RequestMapping(value = {"statisticsImage2"})
    public ResponseEntity<?> statisticsGraphicsPage(Image image, HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (("3".equals(image.getActionType() + "") && StringUtils.isNotBlank(image.getUserId()))
                || ("2".equals(image.getActionType() + ""))
                || ("1".equals(image.getActionType() + ""))
                || (image.getActionType() == null)) {
            if (image.getCreateDate() != null && image.getCreateEndDate() != null){
                Map map;
                image.setImageUserUpdateStartDate(image.getCreateDate());
                image.setImageUserUpdateEndDate(image.getCreateEndDate());
                image.setCreateDate(null);
                image.setCreateEndDate(null);
                map = imageUserService.statisticsGraphicsPage(image);
                builder.add(map);
            }else {
                builder.error();
                builder.message("开始和结束时间createDate、xz不能为空");
            }

        } else {
            builder.error();
            builder.message("actionType为1、2、3，null，type = 1或者null表示自己，actionType=2表示全部,actionType=3时，必须上传userId");
        }
        return builder.build();
    }

    @RequestMapping(value = {"rollback"})
    public ResponseEntity<?> rollback(String imageId, String describe,@RequestParam(defaultValue = "0",required = false) Integer difficult) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        imageUserService.rollback(imageId,describe,difficult);
        builder.message("退回成功");
        return builder.build();
    }

    public Page<Image>  setHospitalCode(Page<Image> page){
        List<Image> imageList = page.getList();
        if (imageList != null && imageList.size() > 0) {
            for (Image image:imageList){
                String hospitalId = image.getHospitalId();
                if (StringUtils.isNotBlank(hospitalId)){
                    String hospitalCode = MemoryCache.getHospitalCodeCahce(hospitalId);
                    if (StringUtils.isNotBlank(hospitalCode))
                        image.setHospitalCode(hospitalCode);
                    else {
                        Office office = officeService.get(hospitalId);
                        MemoryCache.putHospitalCode(hospitalId,office.getCode());
                        image.setHospitalCode(office.getCode());
                    }
                }
            }
        }
        return page;
    }


    public Page<Image> isReadable(Page<Image> page) {
        List<Image> imageList = page.getList();
        if (imageList != null && imageList.size() > 0) {
            for (Image image:imageList){
                String path = image.getPath();
                String prefix=path.substring(path.lastIndexOf(".")+1);
                if ("kfb".equals(prefix) || "tif".equals(prefix) || "ndpi".equals(path)){
                    image.setReadable(true);
                }else {
                    image.setReadable(false);
                }
            }
        }
        return page;
    }

    public Page<Image> getUserNmae(Page<Image> page) {
        List<Image> imageList = page.getList();
        if (imageList != null && imageList.size() > 0) {
            Set<String> userIdSet = new LinkedHashSet<>();
            Set<String> updateIdSet = new LinkedHashSet<>();
            for (Image img : imageList) {
                if (StringUtils.isNotBlank(img.getUserId())){
                    userIdSet.add(img.getUserId());
                }
                if (StringUtils.isNotBlank(img.getIuuBy())){
                    updateIdSet.add(img.getIuuBy());
                }
            }
            if (userIdSet.size() > 0) {
                userIdSet.addAll(updateIdSet);
                List<Map<String, String>> idNameMap = UserUtils.getUserNameListByUserIdList(userIdSet);
                if (idNameMap != null ) {
                    for (Image img : imageList) {
                        String userId = img.getUserId();
                        String iuuBy = img.getIuuBy();
                        if (StringUtils.isNotBlank(userId)) {
                            for (Map map : idNameMap) {
                                String id = (String) map.get("id");
                                if (id.equals(userId)) {
                                    img.setUserName(map.get("name") != null ? (String) map.get("name") : null);
                                }
                                if (id.equals(iuuBy)) {
                                    img.setIuuName(map.get("name") != null ? (String) map.get("name") : null);
                                }
                            }
                        }
                    }
                }
            }
        }
        return page;
    }

    /*
    * 变更所有权
    * */
    @RequestMapping(value = "transferOwnership", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> transferOwnership(@RequestBody OwnershipTranserParamVo paramVo) {
        int row = imageUserService.transferOwnership(paramVo.getImageIdUserIdVos(), paramVo.getTransferUserId());
        ResponseBuilder builder = ResponseBuilder.newInstance();
        builder.add("row", row);
        if (row != 0) {
            builder.message("转移成功！");
        } else builder.message("转移失败！");
        return builder.build();
    }

    /*
   * 切片状态下拉列表
   * */
    @RequestMapping(value = "labelStatusSpinner")
    public ResponseEntity<?> labelStatusSpinner() {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        User user = UserUtils.getUser();
        List list = new ArrayList();
        if (user.isAdmin()){
            List doctor = ReviewStatusUtils.doctor();
            List doctorSubmit = ReviewStatusUtils.doctorSubmit();
            List expert = ReviewStatusUtils.expert();
            List adviser = ReviewStatusUtils.adviser();
            list.addAll(doctor);
            list.addAll(doctorSubmit);
            list.addAll(expert);
            list.addAll(adviser);
        }else if (user.isDirector() || user.isDoctor()) {
            List doctor = ReviewStatusUtils.doctor();
            List doctorSubmit = ReviewStatusUtils.doctorSubmit();
            list.addAll(doctor);
            list.addAll(doctorSubmit);
        }else if (user.isExpert()){
            list.add(ReviewStatusUtils.expert());
        }else if (user.isAdviser()){
            list.add(ReviewStatusUtils.adviser());
        }
        builder.add("list", list);
        return builder.build();
    }


    private List<Map> getImageToTypicalMap(List<Image> list){
        if (list == null)
            new ArrayList<>();
        List listM = new ArrayList();
        for (Image image:list){
            Map map = new HashMap();
            map.put("id",image.getId());
            map.put("path",image.getPath());
            map.put("diseaseId",image.getDiseaseId());
            map.put("name",image.getName());
            map.put("isReadable",image.isReadable());
            map.put("typicalDescribes",image.getTypicalDescribes());
            listM.add(map);
        }
        return listM;
    }

    /*
  * 患者切片描述信息
  * */
    @RequestMapping(value = "describes")
    public ResponseEntity<?> describes(@RequestParam String imageId) {        ResponseBuilder builder = ResponseBuilder.newInstance();
        Image image = imageService.selectByPrimaryKey(imageId);
        if (image != null){
            builder.add("describes",image.getDescribes());
        }else {
            builder.error();
            builder.message("切片不存在");
        }
        return builder.build();
    }

    /*
    * 分配二审池中的切片
    * */
    @RequestMapping(value = "assignment")
    public ResponseEntity<?> assignment(String diseaseId) {
        if (StringUtils.isBlank(diseaseId)) {
            throw new CoreException("需要指定diseaseId，如果要分配多种疾病的切片，diseaseId传all");
        } else if ("all".equals(diseaseId)) {
            diseaseId = null;
        }
        Map map = imageService.reviewAssigment(diseaseId);

        return ResponseBuilder.newInstance().add(map).build();
    }


    @RequestMapping("/detail")
    public ResponseEntity<?> loadImage(@RequestParam String imageId) throws Exception {
        CacheImage cacheImage = cacheImageHolder.getCacheImage(imageId);
//        System.out.println("detail："+cacheImage.getWidth()+" "+cacheImage.getHeight());
        ResponseBuilder builder = ResponseBuilder.newInstance();
        builder.add("ID", imageId)
                .add("Width", cacheImage.getWidth())
                .add("Height", cacheImage.getHeight())
                .add("Overlap", cacheImage.getOverLap())
                .add("TileSize", cacheImage.getTileSize())
                .add("MaxLevel", cacheImage.getMaxLevel())
                .add("MinLevel", cacheImage.getMinLevel())
                .add("path", cacheImage.getPath());
        return builder.build();
    }

    @RequestMapping("/{imageId}_files/{level}/{col}_{row}.{format}")
    public void tile(HttpServletResponse response, @PathVariable String imageId,
                     @PathVariable int level,
                     @PathVariable int col,
                     @PathVariable int row,
                     @PathVariable String format) {
        loadImageTile(response, imageId, level, col, row, format);
    }

    protected void loadImageTile(HttpServletResponse response, String imageId,
                                 int level,
                                 int col,
                                 int row,
                                 String format) {
        try {
            format = format.toLowerCase();
            if ((!"jpeg-".equals(format)) && (!"png-".equals(format))) {
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.info("load tile: [level={},col={},row={}]", level, col, row);
            }
            CacheImage cacheImage = cacheImageHolder.getCacheImage(imageId);
            BufferedImage img = cacheImage.getTile(level, Arrays.asList(col, row));
            //将图片输出给浏览器
            response.setContentType("image/jpeg");
            outputImage(response, img, cacheImage);
        } catch (Exception e) {
            logger.error("Error occur when load tile: [level={},col={},row={}]", level, col, row);
            logger.error("", e);
        }

    }

    @RequestMapping("getLabelInfo")
    public void getLabelInfo(HttpServletResponse response, @RequestParam String imageId, boolean cache) throws Exception {
        if (cache) {
            String cacheDir = PropertyUtil.getProperty("image.label.cache");
            Path filePath = Paths.get(cacheDir + "/" + imageId + ".png");
            if (!Files.exists(filePath)) {
                Path path = Paths.get(cacheDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                CacheImage cacheImage = cacheImageHolder.getCacheImage(imageId);
                BufferedImage img = cacheImage.getLabelInfo(cacheImage.getPath());
                OutputStream out = new FileOutputStream(filePath.toFile().getAbsolutePath());
                ImageIO.write(img, "png", out);
            }

            BufferedImage img = ImageIO.read(new FileInputStream(filePath.toFile().getAbsolutePath()));
            if (img != null) {
                //将图片输出给浏览器
                response.setContentType("image/jpeg");
                ImageIO.write(img, "png", response.getOutputStream());
            }
        } else {
            CacheImage cacheImage = cacheImageHolder.getCacheImage(imageId);
            BufferedImage img = cacheImage.getLabelInfo(cacheImage.getPath());
            response.setContentType("image/jpeg");
            ImageIO.write(img, "png", response.getOutputStream());
        }

    }


    @RequestMapping(value = "loadImageMax")
    public void loadImageMax(HttpServletResponse response, String imageId, String maxSize) {

        try {
            BufferedImage img;
            CacheImage cacheImage = cacheImageHolder.getCacheImage(imageId);
            if (StringUtils.isNotBlank(maxSize) && Integer.parseInt(maxSize) <= 8000)
                img = cacheImage.getThumbnailImage(Integer.parseInt(maxSize));
            else img = cacheImage.getThumbnailImage(2000);
            outputImage(response, img, cacheImage);

        } catch (Exception e) {
            logger.error("Error occur when load tile: [maxSize={}]", maxSize);
            logger.error("", e);
        }
    }

    @RequestMapping(value = "loadImageMaxKfb")
    public void loadImageMaxKfb(HttpServletResponse response, String imageId, String scale, String x, String y) {
        try {
            BufferedImage img;
            KfbImage cacheImage = (KfbImage) cacheImageHolder.getCacheImage(imageId);
            img = cacheImage.getThumbnailImage(Integer.parseInt(scale));
            outputImage(response, img, cacheImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outputImage(HttpServletResponse response, BufferedImage img, CacheImage cacheImage) throws IOException {


        //将图片输出给浏览器
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        if (img != null) {
            if (cacheImage.getType().equals(ImageType.TIF) && Boolean.valueOf(PropertyUtil.getProperty("pathology.image.isZip"))) {
                ImageOutputStream ios = ImageIO.createImageOutputStream(out);

                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
                writer.setOutput(ios);
                Float quality = 1f;
                if ("kfb".equals(cacheImage.getPath().split("[.]")[1])) {
                    quality = 0.7f;
                } else if ("svs".equals(cacheImage.getPath().split("[.]")[1])) {
                    quality = 0.5f;
                } else quality = Float.valueOf(PropertyUtil.getProperty("pathology.image.quantity"));
                JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) writer.getDefaultWriteParam();
                jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
                jpegParams.setCompressionQuality(quality);
                // write the image
                try {
                    writer.write(null, new IIOImage(img, null, null), jpegParams);
                } catch (Exception e) {
                    logger.error("outputImage:", e);
                    e.printStackTrace();
                }
//                ImageIO.write(img,"jpeg",response.getOutputStream());

//                FileCopyUtils.copy(new FileInputStream(img.geti), response.getOutputStream());
//                resp.setHeader("Content-Type", "application/octet-stream");
            } else {
                ImageIO.write(img, "jpeg", out);
            }
        } else {
            throw new NullPointerException("in outputImage function，the parameter img is null");
        }

    }


    @RequestMapping(value = "hospitalIcon")
    public void getHospitalIcon(HttpServletResponse response) throws IOException {
        String path = "classpath:hospital-icon/*";
        ApplicationContext context = ApplicationContextHolder.getApplicationContext();
        Resource[] resources = context.getResources(path);
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition","attachment; filename="+"hospitalIcon.zip");
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        try {
            if (resources != null && resources.length > 0){
                for (Resource resource:resources){
                    String name = resource.getFilename();
                    ZipEntry zipEntry = new ZipEntry(name);
                    out.putNextEntry(zipEntry);
                    InputStream inputStream = resource.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    while ( (len = inputStream.read(bytes)) != -1){
                        out.write(bytes,0,len);
                    }
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            out.flush();
            out.close();
            response.flushBuffer();
        }
    }

    @RequestMapping(value = "imageInitial")
    public ResponseEntity<?> getImageInitial(Image image,HttpServletRequest request,HttpServletResponse response){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        CommonExample example = new CommonExample(Image.class);
        CommonExample.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Image.getFieldDelFlag(),Image.DEL_FLAG_NORMAL);
        //搜索没有分配过的切片
        if (image.getAllocation() != null && (image.getAllocation() == 0 || image.getAllocation() == 2))
            criteria.andEqualTo(Image.getFieldAllocation(),image.getAllocation()+"");
        else {
            //如果没有搜索条件，检索出待自动分配和待手动分配的片子
            List list = new ArrayList();
            list.add("0");
            list.add("2");
            criteria.andIn(Image.getFieldAllocation(),list);
        }
        if (StringUtils.isNotBlank(image.getHospitalId()))
            criteria.andEqualTo(Image.getFieldHospitalId(),image.getHospitalId());
        //是否预测
        if (image.getAiPredict() != null)
            criteria.andEqualTo(Image.getFiedAiPredict(),image.getAiPredict()+"");
        //影像编号
        if (StringUtils.isNotBlank(image.getName()))
            criteria.andLike(Image.getFieldName(),"%"+image.getName()+"%");
        //备注
        if (StringUtils.isNotBlank(image.getRemarks()))
            criteria.andLike(Image.getFieldRemarks(),"%"+image.getRemarks()+"%");
        //病理号
        if (StringUtils.isNotBlank(image.getMedicalRecordNumber()))
            criteria.andLike(Image.getFieldMedicalRecordNumber(),"%"+image.getMedicalRecordNumber()+"%");
        //搜索时间
        if (image.getCreateDate() != null && image.getCreateEndDate() != null)
            criteria.andBetween(Image.getFieldCreateDate(),DateUtils.formatDateTime(image.getCreateDate()),DateUtils.formatDateTime(image.getCreateEndDate()));
        else if (image.getCreateDate() != null)
            criteria.andGreaterThanOrEqualTo(Image.getFieldCreateDate(),DateUtils.formatDateTime(image.getCreateDate()));
        else if (image.getCreateEndDate() != null)
            criteria.andLessThanOrEqualTo(Image.getFieldCreateDate(),DateUtils.formatDateTime(image.getCreateEndDate()));
        //配置项
        if (StringUtils.isNotBlank(image.getDiseaseId())){
            List<String> childIds = coreDiseaseService.getChildsByParentIdAndCategory(image.getDiseaseId(),Constant.CATEGORY_DISEASE);
            if (childIds.size() == 0)
                childIds.add("");
            criteria.andIn(Image.getFieldDiseaseId(),childIds);
        }
        Page page = new Page(request, response);
        example.setPage(page);
        List<Image> imageList = imageService.selectByExample(example);
        if (imageList == null)
            imageList = new ArrayList<>();
        page.setList(imageList);
        setDiseaseName(page);
        setHospitalId(page);
        builder.add("list", page.getList());
        builder.add("total", page.getCount());
        return builder.build();
    }

    public void setDiseaseName(Page<Image> page){
        List<Image> imageList = page.getList();
        String keyName = Constant.CACHE_IMAGE_COMBINATION_DISEASE_NAME;
        if (imageList != null ){
            for (Image image:imageList){
                String diseaseId = image.getDiseaseId();
                String diseaseName = RedisUtils.hget(keyName,diseaseId);
                if (StringUtils.isNotBlank(diseaseName))
                    image.setDiseaseName(diseaseName);
                else if (StringUtils.isNotBlank(diseaseId)){
                    List<Disease> diseaseList = diseaseCustomService.selectImageIdListByHospitalIdAndDiseaseId(diseaseId);
                    String organName = null;
                    String dyeingName = null;
                    String regionName = null;
                    if (diseaseList != null){
                        for (Disease disease : diseaseList){
                            if (disease.getCategory().equals(Constant.CATEGORY_ORGAN))
                                organName = disease.getName();
                            else if (disease.getCategory().equals(Constant.CATEGORY_DYEING))
                                dyeingName =  disease.getName();
                            else if (disease.getCategory().equals(Constant.CATEGORY_RIGION))
                                regionName = disease.getName();
                        }
                    }
                    diseaseName = organName+"/"+dyeingName+"/"+regionName;
                    image.setDiseaseName(diseaseName);
                    RedisUtils.hset(keyName,diseaseId,diseaseName);
                }
            }
        }
    }

    public void setHospitalId(Page<Image> page){
        List<Image> imageList = page.getList();
        if (imageList != null ){
            for (Image image:imageList){
                String hospitalId = image.getHospitalId();
                if (StringUtils.isNotBlank(hospitalId)){
                    Office office = (Office) UserUtils.getCache(hospitalId);
                    if (office != null){
                        image.setHospitalName(office.getName());
                    }else {
                        office = officeService.get(hospitalId);
                        UserUtils.putCache(hospitalId,office);
                        image.setHospitalName(office.getName());
                    }
                }
            }
        }
    }

    public void setSpinnerIdName(Page<Image> page){
        List<Image> imageList = page.getList();
        if (imageList != null ){
            for (Image image:imageList){
                Byte ownership = image.getOwnership();
                Integer reviewStage = image.getReviewStage();
                Integer initialReviewStage = image.getInitialReviewStage();
                Integer labelStatus = image.getLabelStatus();
                if (ownership != null && reviewStage != null && initialReviewStage != null){
                    Map<String,String> map = ReviewStatusUtils.getSpinnerIdByROIL(reviewStage,ownership,initialReviewStage,labelStatus);
                    if (map != null){
                        String spinnerId = map.get("spinnerId");
                        String name = map.get("name");
                        image.setSpinnerId(spinnerId);
                        image.setStatusName(name);
                    }else {
                        image.setSpinnerId(null);
                        image.setStatusName("未知状态");
                    }
                }else {
                    image.setSpinnerId(null);
                    image.setStatusName("未知状态");
                }
            }
        }
    }

    /**
     * 单个删除和批量删除
     *
     * */
    @RequestMapping(value = "deleteImage")
    public ResponseEntity<?> deleteImage(@RequestParam List<String> imageIdList){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (imageIdList.size() > 0){
            imageService.deleteByPrimaryKeyList(imageIdList);
            builder.message("删除成功！");
        }else {
            builder.error();
            builder.message("没有上传切片ID");
        }
        return builder.build();
    }

    /**
     * 单个分配和批量分配
     *
     * */
    @RequestMapping(value = "allocationNew")
    public ResponseEntity<?> allocationNew(@RequestParam List<String> imageIdList,
                                @RequestParam List<String> userIdList,
                                @RequestParam String spinnerId){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (imageIdList.size() == 0){
            builder.error();
            builder.message("没有上传切片ID");
            return builder.build();
        }
        if (userIdList.size() == 0){
            builder.error();
            builder.message("没有上传用户ID");
            return builder.build();
        }

        Map checkMap = checkAllocation(spinnerId,userIdList);
        boolean pass = (boolean) checkMap.get("pass");
        if (!pass){
            String message = (String) checkMap.get("message");
            builder.message(message);
            return builder.build();
        }

        List<Map<String,String>> statusList = ReviewStatusUtils.initAllocationStatus();
        Map<String,String> statusMap = null;
        for (Map<String,String> map:statusList){
            String existSpinnerId = map.get("spinnerId");
            if (existSpinnerId.equals(spinnerId)){
                statusMap = map;
                break;
            }
        }
        if (statusMap != null){
            Integer initialReviewStage = Integer.parseInt(statusMap.get("initialReviewStage"));
            Integer  reviewStage = Integer.parseInt(statusMap.get("reviewStage"));
            Integer  labelStatus = Integer.parseInt(statusMap.get("labelStatus"));
            imageUserService.allocationNewList(imageIdList,userIdList,initialReviewStage,reviewStage,labelStatus);
            builder.message("分配成功");
            return builder.build();
        }else {
            builder.error();
            builder.message("没有找到"+spinnerId+"对应的状态");
            return builder.build();
        }

    }

    @RequestMapping(value = "initAllocationStatus")
    public ResponseEntity<?> initAllocationStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map<String,String>> statusList = ReviewStatusUtils.initAllocationStatus();
        builder.add("statusList",statusList);
        return builder.build();
    }

    @RequestMapping(value = "againAllocationStatus")
    public ResponseEntity<?> againAllocationStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map<String,String>> statusList = ReviewStatusUtils.initAllocationStatus();
        builder.add("statusList",statusList);
        return builder.build();
    }

    @RequestMapping(value = "expertAllocationStatus")
    public ResponseEntity<?> expertAndAdvisorAllocationStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Map<String, String> statusList = ReviewStatusUtils.submit11();
        List list = new ArrayList();
        list.add(statusList);
        builder.add("statusList",list);
        return builder.build();
    }

    /**
     * 数据再分配，单个分配和批量分配
     *
     * */
    @RequestMapping(value = "allocationAgain" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> allocationAgain(@RequestBody AgainAllocationVo againAllocationVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (againAllocationVo.getToUserIdList() == null || againAllocationVo.getToUserIdList().size() == 0){
            builder.error();
            builder.message("分配用户不能为空");
            return builder.build();
        }
        if (StringUtils.isBlank(againAllocationVo.getSpinnerId())){
            builder.error();
            builder.message("分配状态不能为空");
            return builder.build();
        }
        if (againAllocationVo.getImageUserIdVoList() == null || againAllocationVo.getImageUserIdVoList().size() == 0){
            builder.error();
            builder.message("分配切片不能为空");
            return builder.build();
        }
        Map checkMap = checkAllocation(againAllocationVo.getSpinnerId(),againAllocationVo.getToUserIdList());
        boolean pass = (boolean) checkMap.get("pass");
        if (!pass){
            String message = (String) checkMap.get("message");
            builder.message(message);
            return builder.build();
        }
        List<Map<String,String>> statusList = ReviewStatusUtils.initAllocationStatus();
        Map<String,String> statusMap = null;
        for (Map<String,String> map:statusList){
            String existSpinnerId = map.get("spinnerId");
            if (existSpinnerId.equals(againAllocationVo.getSpinnerId())){
                statusMap = map;
                break;
            }
        }
        Integer initialReviewStage = Integer.parseInt(statusMap.get("initialReviewStage"));
        Integer reviewStage = Integer.parseInt(statusMap.get("reviewStage"));
        Integer labelStatus = Integer.parseInt(statusMap.get("labelStatus"));
        imageUserService.allocationAgain(againAllocationVo.getImageUserIdVoList(),againAllocationVo.getToUserIdList(),initialReviewStage,reviewStage,labelStatus);
        builder.message("分配成功");
        return builder.build();
    }

    @RequestMapping(value = "againAllocationSearchStatus")
    public ResponseEntity<?> againAllocationSearchStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map<String,String>> statusList = ReviewStatusUtils.againAllocationSearchStatus();
        builder.add("statusList",statusList);
        return builder.build();
    }

    public Map checkAllocation(String spinnerId,List<String> userIdList){
        Map map = new HashMap();
        for (String userId:userIdList){
            User user = UserUtils.get(userId);
            if (user.isAdviser()){
                if (!(Constant.LABEL_ADVISER_ON.equals(spinnerId) || Constant.LABEL_ADVISER_UN.equals(spinnerId))){
                    map.put("pass",false);
                    map.put("message","分配给顾问时，分配状态只能是顾问待审核");
                    return map;
                }
            } else if (user.isExpert()){
                if (!(Constant.LABEL_EXPERT_ON.equals(spinnerId) || Constant.LABEL_EXPERT_UN.equals(spinnerId))){
                    map.put("pass",false);
                    map.put("message","分配给专家时，分配状态只能是专家待审核");
                    return map;
                }
            }else if (user.isDirector()){
                if (!(Constant.LABEL_ONE_ON.equals(spinnerId) || Constant.LABEL_ONE_UN.equals(spinnerId)
                        || Constant.LABEL_TWO_ON.equals(spinnerId) || Constant.LABEL_TWO_UN.equals(spinnerId))){
                    map.put("pass",false);
                    map.put("message","分配给主任时，分配状态只能是一审待标注或者二审待标注");
                    return map;
                }
            }else if (user.isDoctor()){
                if (!(Constant.LABEL_ONE_ON.equals(spinnerId) || Constant.LABEL_ONE_UN.equals(spinnerId)
                        || Constant.LABEL_TWO_ON.equals(spinnerId) || Constant.LABEL_TWO_UN.equals(spinnerId))){
                    map.put("pass",false);
                    map.put("message","分配给医生时，分配状态只能是一审待标或者或者二审待标注");
                    return map;
                }
            }
        }
        map.put("pass",true);
        map.put("message","");
        return map;
    }

    /**
     * 查询待标注和正在标注的数据
     * */
    @RequestMapping(value = "imageUserList")
    public ResponseEntity<?> imageUserList(Image image,String spinnerId,HttpServletRequest request,
                                           HttpServletResponse response){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Image imageParm = new Image();
        imageParm.setSourceHospitalIds(image.getSourceHospitalIds());
        setUserIdList(image,imageParm);
        imageParm.setName(image.getName());
        imageParm.setRemarks(image.getRemarks());
        imageParm.setCreateDate(image.getCreateDate());
        imageParm.setCreateEndDate(image.getCreateEndDate());
        //查询待标注和正在标注的数据
        imageParm.setOwnership((byte) 1);
        if (StringUtils.isNotBlank(image.getDiseaseId())){
            List<String> childIds = coreDiseaseService.getChildsByParentIdAndCategory(image.getDiseaseId(),Constant.CATEGORY_DISEASE);
            if (childIds.size() == 0)
                childIds.add("");
            imageParm.setDiseaseIdList(childIds);
        }
        if (StringUtils.isNotBlank(spinnerId)){
            List<Map<String,String>> statusList = ReviewStatusUtils.againAllocationSearchStatus();
            Map<String,String> statusMap = null;
            for (Map<String,String> map:statusList){
                String existSpinnerId = map.get("spinnerId");
                if (existSpinnerId.equals(spinnerId)){
                    statusMap = map;
                    break;
                }
            }
            Integer initialReviewStage = Integer.parseInt(statusMap.get("initialReviewStage"));
            Integer reviewStage = Integer.parseInt(statusMap.get("reviewStage"));
            Integer labelStatus = Integer.parseInt(statusMap.get("labelStatus"));
            imageParm.setInitialReviewStage(initialReviewStage);
            imageParm.setReviewStage(reviewStage);
            imageParm.setLabelStatus(labelStatus);

        }
        image.setDelFlag(Image.DEL_FLAG_NORMAL);
        Page<Image> page;
        imageParm.setPage(new Page<>(request, response));
        page = imageUserService.getImagesByUserForManage(imageParm);
        if (page != null) {
            getUserNmae(page);
            isReadable(page);
            //需要优化 start
            setDiseaseName(page);
            setHospitalId(page);
            setSpinnerIdName(page);
            //需要优化 end
            builder.add("list", page.getList());
            builder.add("total", page.getCount());
        } else {
            builder.add("list", new ArrayList<>());
            builder.add("total", 0);
        }
        return builder.build();
    }


    /**
     * 数据再分配，单个分配和批量分配
     *
     * */
    @RequestMapping(value = "allocationExpert",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> allocationExpert(@RequestBody AgainAllocationVo againAllocationVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (againAllocationVo.getToUserIdList() == null || againAllocationVo.getToUserIdList().size() == 0){
            builder.error();
            builder.message("分配用户不能为空");
            return builder.build();
        }
        if (againAllocationVo.getImageUserIdVoList() == null || againAllocationVo.getImageUserIdVoList().size() == 0){
            builder.error();
            builder.message("分配切片不能为空");
            return builder.build();
        }
        for (String userId:againAllocationVo.getToUserIdList()){

            User user = UserUtils.get(userId);
            if (user != null){
                if (Constant.USER_EXPERT.equals(user.getUserType())){
                    //专家待审核
                    againAllocationVo.setSpinnerId(Constant.LABEL_EXPERT_UN);
                }
                else if (Constant.USER_ADVISOR.equals(user.getUserType())){
                    //顾问待审核
                    againAllocationVo.setSpinnerId(Constant.LABEL_ADVISER_UN);
                }
                List<Map<String,String>> statusList = ReviewStatusUtils.expertAndAdvisorAllocationStatus();
                Map<String,String> statusMap = null;
                for (Map<String,String> map:statusList){
                    String existSpinnerId = map.get("spinnerId");
                    if (existSpinnerId.equals(againAllocationVo.getSpinnerId())){
                        statusMap = map;
                        break;
                    }
                }
                Integer initialReviewStage = Integer.parseInt(statusMap.get("initialReviewStage"));
                Integer reviewStage = Integer.parseInt(statusMap.get("reviewStage"));
                Integer labelStatus = Integer.parseInt(statusMap.get("labelStatus"));
                List<String> toUserIdList = new ArrayList<>();
                toUserIdList.add(userId);
                imageUserService.allocationExpert(againAllocationVo.getImageUserIdVoList(),toUserIdList,initialReviewStage,reviewStage,labelStatus);
            }else {
                throw  new CoreException("用户ID不存在"+userId);
            }
        }
        builder.message("分配成功");
        return builder.build();
    }

    /**
     * 默认返回二审已经提交且没有分配过给userId的片子
     * */
    @RequestMapping(value = "imageListToExpertAdvisor")
    public ResponseEntity<?> imageListToExpertAdvisor(Image imageParam,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<String> diseaseIds = null;
        if (StringUtils.isNotBlank(imageParam.getDiseaseId())){
            List<String> childIds = coreDiseaseService.getChildsByParentIdAndCategory(imageParam.getDiseaseId(),Constant.CATEGORY_DISEASE);
            if (childIds.size() == 0)
                childIds.add("");
            diseaseIds = childIds;
        }
        Image image = new Image();
        image.setUserId(imageParam.getUserId());
        setUserIdList(imageParam,image);
        image.setDiseaseIdList(diseaseIds);
        image.setExcludeUserId(imageParam.getExcludeUserId());
        image.setName(imageParam.getName());
        image.setMedicalRecordNumber(imageParam.getMedicalRecordNumber());
        image.setRemarks(imageParam.getRemarks());
        image.setCreateDate(imageParam.getCreateDate());
        image.setCreateEndDate(imageParam.getCreateEndDate());
        image.setSourceHospitalIds(imageParam.getSourceHospitalIds());
        Page<Image> page;
        image.setPage(new Page<>(request, response));

        page = imageUserService.imageListToExpertAdvisor(image);
        if (page != null) {
            getUserNmae(page);
            isReadable(page);
            //需要优化 start
            setDiseaseName(page);
            setHospitalId(page);
            setSpinnerIdName(page);
            //需要优化 end
            builder.add("imageList", page.getList());
            builder.add("total", page.getCount());
        } else {
            builder.add("imageList", new ArrayList<>());
            builder.add("total", 0);
        }
        return builder.build();
    }

    @RequestMapping(value = "switchAllocation",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> switchAllocation(@RequestBody SwitchAllocationVo switchAllocationVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (switchAllocationVo.getAllocation() !=0 && switchAllocationVo.getAllocation() !=2){
            builder.error();
            builder.message("allocation只能为0或者2");
            return builder.build();
        }
        if (switchAllocationVo.getImageIdList() == null || switchAllocationVo.getImageIdList().size() == 0){
            builder.error();
            builder.message("切片ID不能为空");
            return builder.build();
        }
        imageService.switchAllocation(switchAllocationVo.getAllocation(),switchAllocationVo.getImageIdList());
        if (switchAllocationVo.getAllocation() == 0)
            builder.message("成功更新为自动分配状态");
        else if (switchAllocationVo.getAllocation() == 2)
            builder.message("成功更新为非自动分配状态");
        return builder.build();
    }

    /**
     * 一审提交的数据
     * */
    @RequestMapping(value = "imageListPool")
    public ResponseEntity<?> imageListPool(Image imageParam,HttpServletRequest request, HttpServletResponse response){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<String> diseaseIds = null;
        if (StringUtils.isNotBlank(imageParam.getDiseaseId())){
            List<String> childIds = coreDiseaseService.getChildsByParentIdAndCategory(imageParam.getDiseaseId(),Constant.CATEGORY_DISEASE);
            if (childIds.size() == 0)
                childIds.add("");
            diseaseIds = childIds;
        }
        Image image = new Image();
        setUserIdList(imageParam,image);
        image.setDiseaseIdList(diseaseIds);
        image.setExcludeUserId(imageParam.getExcludeUserId());
        image.setName(imageParam.getName());
        image.setMedicalRecordNumber(imageParam.getMedicalRecordNumber());
        image.setRemarks(imageParam.getRemarks());
        image.setCreateDate(imageParam.getCreateDate());
        image.setCreateEndDate(imageParam.getCreateEndDate());
        image.setSourceHospitalIds(imageParam.getSourceHospitalIds());
        Page<Image> page;
        image.setPage(new Page<>(request, response));

        page = reviewPoolImageService.imageListPool(image);
        if (page != null) {
            getUserNmae(page);
            isReadable(page);
            //需要优化 start
            setDiseaseName(page);
            setHospitalId(page);
            setSpinnerIdName(page);
            //需要优化 end
            builder.add("imageList", page.getList());
            builder.add("total", page.getCount());
        } else {
            builder.add("imageList", new ArrayList<>());
            builder.add("total", 0);
        }
        return builder.build();
    }

    public Image setUserIdList(Image imageParam,Image image){
        if (StringUtils.isNotBlank(imageParam.getUserId()))
            image.setUserId(imageParam.getUserId());
        else if (StringUtils.isNotBlank(imageParam.getHospitalId())){
            List<Map<String, String>> userMapList = UserUtils.getUserIdNameTypeByHospitalId(imageParam.getHospitalId(),null);
            List<String> userIdList = UserUtils.getUserIdListFromUserMapList(userMapList);
            image.setUserIdList(userIdList);
        }
        return image;
    }

    @RequestMapping(value = "imageWithdraw",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> imageWithdraw(@RequestBody ImageWithdrawVo imageWithdrawVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<ImageUserIdVo> imageUserIdVoList = imageWithdrawVo.getImageUserIdVoList();
        if (imageWithdrawVo.getImageUserIdVoList() == null || imageUserIdVoList.size() == 0){
            builder.error();
            builder.message("没有上传切信息");
            return builder.build();
        }

        for (ImageUserIdVo imageUserIdVo : imageUserIdVoList){
            if (StringUtils.isNotBlank(imageUserIdVo.getImageId()) && StringUtils.isNotBlank(imageUserIdVo.getUserId())
                    && StringUtils.isNotBlank(imageUserIdVo.getSpinnerId())){
                String spinnerId = imageUserIdVo.getSpinnerId();
                String imageId = imageUserIdVo.getImageId();
                String userId = imageUserIdVo.getUserId();
                if (Constant.LABEL_ADVISER_UN.equals(spinnerId) || Constant.LABEL_ADVISER_ON.equals(spinnerId)){
                    //顾问撤回
                    imageService.withdrawToSubmit20(imageId,userId);
                }
                else if (Constant.LABEL_EXPERT_UN.equals(spinnerId) || Constant.LABEL_EXPERT_ON.equals(spinnerId)){
                    //专家撤回
                    imageService.withdrawToSubmit11(imageId,userId);
                }else if (Constant.LABEL_TWO_UN.equals(spinnerId) || Constant.LABEL_TWO_ON.equals(spinnerId)){
                    //二审撤回
                    imageService.withdrawToSubmit10(imageId,userId);
                }
                else if (Constant.LABEL_ONE_UN.equals(spinnerId) || Constant.LABEL_ONE_ON.equals(spinnerId)){
                    //一审撤回
                    imageService.withdrawToUnAllocation(imageId,userId);
                }else {
                    builder.error();
                    builder.message("撤回状态不正确");
                    return builder.build();
                }

            }else {
                builder.error();
                builder.message("切片信息不完整");
                return builder.build();
            }

        }
        builder.message("撤回成功");
        return builder.build();
    }

    @RequestMapping(value = "allocationReviewPoolStatus")
    public ResponseEntity<?> allocationReviewPoolStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        builder.add("list",ReviewStatusUtils.allocationReviewPoolStatus());
        return builder.build();
    }


    /**
     * 二审池分配
     *
     * */
    @RequestMapping(value = "allocationReviewPool" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> allocationReviewPool(@RequestBody AgainAllocationVo againAllocationVo){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if (againAllocationVo.getToUserIdList() == null || againAllocationVo.getToUserIdList().size() == 0){
            builder.error();
            builder.message("分配用户不能为空");
            return builder.build();
        }
        if (StringUtils.isBlank(againAllocationVo.getSpinnerId())){
            builder.error();
            builder.message("分配状态不能为空");
            return builder.build();
        }
        if (againAllocationVo.getImageUserIdVoList() == null || againAllocationVo.getImageUserIdVoList().size() == 0){
            builder.error();
            builder.message("分配切片不能为空");
            return builder.build();
        }
        Map checkMap = checkAllocation(againAllocationVo.getSpinnerId(),againAllocationVo.getToUserIdList());
        boolean pass = (boolean) checkMap.get("pass");
        if (!pass){
            String message = (String) checkMap.get("message");
            builder.message(message);
            return builder.build();
        }

        List<Map<String,String>> statusList = ReviewStatusUtils.allocationReviewPoolStatus();
        Map<String,String> statusMap = null;
        for (Map<String,String> map:statusList){
            String existSpinnerId = map.get("spinnerId");
            if (existSpinnerId.equals(againAllocationVo.getSpinnerId())){
                statusMap = map;
                break;
            }
        }
        if (statusMap != null){
            Integer initialReviewStage = Integer.parseInt(statusMap.get("initialReviewStage"));
            Integer reviewStage = Integer.parseInt(statusMap.get("reviewStage"));
            Integer labelStatus = Integer.parseInt(statusMap.get("labelStatus"));
            imageUserService.allocationReviewPool(againAllocationVo.getImageUserIdVoList(),againAllocationVo.getToUserIdList(),initialReviewStage,reviewStage,labelStatus);
            builder.message("分配成功");
        }else {
            builder.error();
            builder.message("分配状态不正确");
        }
        return builder.build();
    }

    @RequestMapping(value = "imageAllStatus")
    public ResponseEntity<?> imageAllStatus(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        builder.add("list",ReviewStatusUtils.imageAllStatus());
        return builder.build();
    }

    /**
     * 数据看板
     * */
    @RequestMapping(value = "imageAll")
    public ResponseEntity<?> imageAll(Image imageParam,HttpServletRequest request, HttpServletResponse response){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<String> diseaseIds = null;
        if (StringUtils.isNotBlank(imageParam.getDiseaseId())){
            List<String> childIds = coreDiseaseService.getChildsByParentIdAndCategory(imageParam.getDiseaseId(),Constant.CATEGORY_DISEASE);
            if (childIds.size() == 0)
                childIds.add("");
            diseaseIds = childIds;
        }
        Image image = new Image();
        setUserIdList(imageParam,image);
        image.setDiseaseIdList(diseaseIds);
        image.setExcludeUserId(imageParam.getExcludeUserId());
        image.setName(imageParam.getName());
        image.setMedicalRecordNumber(imageParam.getMedicalRecordNumber());
        image.setRemarks(imageParam.getRemarks());
        image.setCreateDate(imageParam.getCreateDate());
        image.setCreateEndDate(imageParam.getCreateEndDate());
        image.setSourceHospitalIds(imageParam.getSourceHospitalIds());
        if (StringUtils.isNotBlank(imageParam.getSpinnerId())){
            Map<String,String> map = ReviewStatusUtils.getReviewStageMapBySpinnerId(imageParam.getSpinnerId(),ReviewStatusUtils.imageAllStatus());
            //查询已经提交的数据
            if (imageParam.getSpinnerId().startsWith("submit")){
                String initialReviewStage = map.get("initialReviewStage");
                String ownership = map.get("ownership");
                image.setInitialReviewStage(Integer.parseInt(initialReviewStage));
                image.setOwnership(Byte.parseByte(ownership));
            }else {
                String initialReviewStage = map.get("initialReviewStage");
                String reviewStage = map.get("reviewStage");
                String labelStatus = map.get("labelStatus");
                image.setInitialReviewStage(Integer.parseInt(initialReviewStage));
                image.setReviewStage(Integer.parseInt(reviewStage));
                image.setLabelStatus(Integer.parseInt(labelStatus));
                image.setOwnership((byte) 1);
            }

        }

        Page<Image> page;
        image.setPage(new Page<>(request, response));

        page = imageUserService.imageAll(image);
        if (page != null) {
            getUserNmae(page);
            isReadable(page);
            setDiseaseName(page);
            setHospitalId(page);
            setSpinnerIdName(page);
            builder.add("imageList", page.getList());
            builder.add("total", page.getCount());
        } else {
            builder.add("imageList", new ArrayList<>());
            builder.add("total", 0);
        }
        return builder.build();
    }


}
