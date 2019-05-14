package com.thorough.core.modules.pathology.thread;

import com.thorough.core.modules.pathology.model.entity.ImageUserOverdue;
import com.thorough.core.modules.pathology.service.ImageUserOverdueService;
import com.thorough.core.modules.pathology.service.ImageUserService;
import com.thorough.library.mybatis.persistence.model.dao.CommonExample;
import com.thorough.library.utils.DateUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Component
public class ImageUserOverdueThread  implements InitializingBean,DisposableBean {

    ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);
    @Autowired
    ImageUserOverdueService imageUserOverdueService;
    @Autowired
    ImageUserService imageUserService;

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    CommonExample example = new CommonExample(ImageUserOverdue.class);
                    example.createCriteria().
                            andEqualTo(ImageUserOverdue.getFieldDelFlag(),"0");
                    List<ImageUserOverdue> list = imageUserOverdueService.selectByExample(example);
                    Date current = new Date();
                    if (list!=null && list.size() > 0){
                        for (ImageUserOverdue imageUserOverdue:list){
                            Date date = imageUserOverdue.getCreateDate();
                            double day = DateUtils.getDistanceOfTwoDate(date,current);
                            if (day >= 5){
                                try {
                                    imageUserService.updateImageUserAndOverdue(imageUserOverdue);
                                }catch (Exception e){
                                    //重新试一次
                                    try {
                                        imageUserService.updateImageUserAndOverdue(imageUserOverdue);
                                    }catch (Exception e1){
                                        e.printStackTrace();
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        }
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },0,30, TimeUnit.MINUTES);
    }

    @Override
    public void destroy() throws Exception {
        scheduledExecutorService.shutdown();
    }

}
