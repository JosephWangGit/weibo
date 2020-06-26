package com.weibo.weibo.entity;

public class Status {

    private Integer id;

    private String content;

    private Integer publisherId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", publisherId=" + publisherId +
                '}';
    }
}
