#185需要执行
ALTER TABLE `pathology_image` ADD `predict_start_date` datetime DEFAULT NULL COMMENT '预测开始时间' AFTER progress;
ALTER TABLE `pathology_image` ADD `predict_end_date` datetime DEFAULT NULL COMMENT '预测完成时间' AFTER predict_start_date;
ALTER TABLE `pathology_image` ADD `medical_record_number` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '病历号' AFTER predict_end_date;
ALTER TABLE `pathology_image_user` ADD `submit_date` datetime DEFAULT NULL COMMENT '一审、二审、专家、顾问提交时间' AFTER rollback_user;
#上线需要清理二审池中已经分配给出去的数据