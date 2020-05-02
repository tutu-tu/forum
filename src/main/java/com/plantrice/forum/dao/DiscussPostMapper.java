package com.plantrice.forum.dao;

import com.plantrice.forum.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//数据访问层
@Mapper
public interface DiscussPostMapper {

    //加userId是为了开发客户的个人主页的功能的我发布的帖子就可以调用这个方法，传入id，
    // 当那个首页上不会传入这个值，默认就是零，当userId为零的时候就不拼接到sql语句中，所以这个是动态的sql语句。
    //offset 起始行的行号  limit一页显示多少行数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    //查询帖子的行数
    int selectDiscussPostRows(@Param("userId") int userId);

    //插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    //通过id查找帖子的所有内容
    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);
}
