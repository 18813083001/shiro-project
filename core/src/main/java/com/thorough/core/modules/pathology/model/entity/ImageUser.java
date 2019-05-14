package com.thorough.core.modules.pathology.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.UserDateDelFlagEntity;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "pathology_image_user")
public class ImageUser extends UserDateDelFlagEntity {

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "ownership")
    private Byte ownership;

    /*
    * 改字段记录片子分配时的状态
    * */
    @Column(name = "review_stage")
    private Integer reviewStage;

    @Column(name = "favorites")
    private Integer favorites;

    @Column(name = "difficult")
    private Integer difficult;

    @Column(name = "difficult_describes")
    private String difficultDescribes;

    @Column(name = "favorites_describes")
    private String favoritesDescribes;

    @Column(name = "rollback")
    private Integer rollback;

    @Column(name = "rollback_describes")
    private String rollbackDescribes;

    @Column(name = "rollback_user")
    private String rollbackUser;


    @Override
    public void preInsert(){
        super.preInsert();
        if (reviewStage == null)
            reviewStage=10;
        if (favorites == null)
            favorites=0;
        if (ownership == null)
            ownership=1;
        if (rollback == null)
            rollback=0;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId == null ? null : imageId.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Byte getOwnership() {
        return ownership;
    }

    public void setOwnership(Byte ownership) {
        this.ownership = ownership;
    }

    public Integer getReviewStage() {
        return reviewStage;
    }

    public void setReviewStage(Integer reviewStage) {
        this.reviewStage = reviewStage;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public Integer getDifficult() {
        return difficult;
    }

    public void setDifficult(Integer difficult) {
        this.difficult = difficult;
    }

    public String getDifficultDescribes() {
        return difficultDescribes;
    }

    public void setDifficultDescribes(String difficultDescribes) {
        this.difficultDescribes = difficultDescribes;
    }

    public String getFavoritesDescribes() {
        return favoritesDescribes;
    }

    public void setFavoritesDescribes(String favoritesDescribes) {
        this.favoritesDescribes = favoritesDescribes;
    }

    public Integer getRollback() {
        return rollback;
    }

    public void setRollback(Integer rollback) {
        this.rollback = rollback;
    }

    public String getRollbackUser() {
        return rollbackUser;
    }

    public void setRollbackUser(String rollbackUser) {
        this.rollbackUser = rollbackUser;
    }

    public String getRollbackDescribes() {
        return rollbackDescribes;
    }

    public void setRollbackDescribes(String rollbackDescribes) {
        this.rollbackDescribes = rollbackDescribes;
    }
}