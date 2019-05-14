package com.thorough.core.modules.sys.controller;

import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.model.entity.Disease;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.ResponseBuilder;
import com.thorough.library.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${adminPath}/sys/disease")
public class DiseaseController extends BaseController{

    @Autowired
    CoreDiseaseService coreDiseaseService;

    @RequestMapping(value = "userOrgan")
    public ResponseEntity<?> getUserOrgan(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map> usersOrganList = coreDiseaseService.getUserOrgan();
        builder.add("usersOrganList",usersOrganList);
        return builder.build();
    }

    /*
    * 可以获取树形结构的所有配置项
    * */
    @RequestMapping(value = {"treeData"})
    public ResponseEntity<?> treeData(Disease disease){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map> list = coreDiseaseService.treeData(disease);
        builder.add("disease",list);
        return builder.build();
    }

    /*
    * 可以获取父类下面的直属孩子配置项（只需要传parentId）
    * 或者父类下面的某一节孩子配置项（传入category 如category=dyeing）
    * */
    @RequestMapping(value = "diseaseList")
    public ResponseEntity<?> getDiseaseListByParentIdOrCategory(Disease disease, @RequestParam(defaultValue = "1",required = false) Integer aiPredict){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<Map> list = null;
        if(disease != null){
            list = coreDiseaseService.getDirectDiseaseListByParentIdAndCategoryAndRole(disease,aiPredict);
        }
        builder.add("list",list);
        return builder.build();
    }

    /*
    * BindingResult result %2转#号
    * */
    @RequiresPermissions(value = "admin")
    @RequestMapping(value = "add")
    public ResponseEntity<?> add(@Valid Disease disease) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        if(StringUtils.isNotBlank(disease.getName())){
            if(StringUtils.isBlank(disease.getRgb())){
                disease.setRgb("#000000");
            }
            String id = coreDiseaseService.save(disease);
            builder.add("id",id);
        }else{
            builder.error();
            builder.message("名称为空");
        }
        return  builder.build();
    }

    @RequiresPermissions(value = "admin")
    @RequestMapping(value = "update")
    public ResponseEntity<?> update(Disease disease, BindingResult result) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        ResponseEntity<?> response = null;
        if(StringUtils.isNotBlank(disease.getId())){
            if(coreDiseaseService.exist(disease.getId())){
                int row = coreDiseaseService.update(disease);
                builder.add("row",row);
                response = builder.build();
            }
            else {
                builder.error();
                builder.message("实体不存在");
                response = builder.build();
            }
        }else {
            builder.error();
            builder.message("ID为空");
            response = builder.build();
        }
        return response;
    }

    @RequiresPermissions(value = "admin")
    @RequestMapping(value = "remove")
    public ResponseEntity<?> remove(@RequestParam(value="id", required=true) String id) {
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Disease disease = new Disease();
        disease.setId(id);
        int rows = coreDiseaseService.delete(disease);
        builder.add("rows",rows);
        return builder.build();
    }

    @RequestMapping(value = "roleDisease")
    public String roleDisease(Model model){
        List<Disease> diseaseList = coreDiseaseService.findAll();
        User user = UserUtils.getUser();
        List<String> roleIds = user.getRoleIdList();
        List<String> roleDiseaseIds = coreDiseaseService.getDiseaseIdsByRoleId(roleIds);
        DiseaseVo diseaseVo = new DiseaseVo();
        diseaseVo.setRoleDiseaseIds(roleDiseaseIds.toString().replace("[","").replace("]","").replace(" ",""));
        model.addAttribute("diseaseList", diseaseList);
        model.addAttribute("diseaseVo", diseaseVo);
        return "/modules/pathology/roleDisease";
    }

    @RequestMapping(value = "saveRoleDisease")
    public String saveRoleDisease(String roleDiseaseIds){
        User user = UserUtils.getUser();
        coreDiseaseService.insertRoleDisease(user.getRoleIdList().get(0).split(",")[0],roleDiseaseIds);

        return "redirect:" + adminPath + "/pathology/disease/roleDisease";
    }

    @RequestMapping(value = {"organ"})
    public ResponseEntity<?> getOrgan(Disease disease){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Map<String,Object> map = coreDiseaseService.getAvailableAndUnavailableDirectDiseaseListByParentId(disease.getParentId(),1);
        builder.add(map);
        return builder.build();
    }

    /*
    * 通过具体的某种疾病id或者分类di
    * */
    @RequestMapping(value = "getParentByChildId")
    public ResponseEntity<?> getParentByChildId(String diseaseId, String category){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        Map map = coreDiseaseService.getParentByChildId(diseaseId,category);
        builder.add(map);
        return builder.build();
    }

    public class DiseaseVo implements Serializable {
        private String roleDiseaseIds;

        public DiseaseVo(){}

        public String getRoleDiseaseIds() {
            return roleDiseaseIds;
        }

        public void setRoleDiseaseIds(String roleDiseaseIds) {
            this.roleDiseaseIds = roleDiseaseIds;
        }
    }

}
