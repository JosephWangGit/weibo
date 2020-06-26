package com.weibo.weibo.mapper;

import com.weibo.weibo.entity.UserLikeStatus;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserLikeStatusMapper {

    @Insert({
            "insert into user_like_status (status_id, user_id)",
            "values (#{statusId,jdbcType=INTEGER}, #{userId,jdbcType=INTEGER})"
    })
    int insert(UserLikeStatus userLikeStatus);

    @Select({
            "select",
            "count(*) from user_like_status",
            "where status_id = #{statusId,jdbcType=INTEGER} and user_id = #{userId,jdbcType=INTEGER}"
    })
    int select(UserLikeStatus userLikeStatus);
}