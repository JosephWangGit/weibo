package com.weibo.weibo.entity;

public class UserLikeStatus {

    private Integer id;

    private Integer statusId;

    private Integer userId;

    public UserLikeStatus() {
    }

    public UserLikeStatus(Integer statusId, Integer userId) {
        this.statusId = statusId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserLikeStatus{" +
                "id=" + id +
                ", statusId=" + statusId +
                ", userId=" + userId +
                '}';
    }
}
