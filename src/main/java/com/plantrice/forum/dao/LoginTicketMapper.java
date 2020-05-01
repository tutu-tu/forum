package com.plantrice.forum.dao;

import com.plantrice.forum.entity.LoginTicket;
import org.apache.ibatis.annotations.*;


@Mapper
public interface LoginTicketMapper {

    //插入一条数据，自动生成的值注入给bean对象，注入给那个属性（keyProperty = "id"），
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    //通过ticket查询，凭证
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    //修改状态 动态sql script表示脚本
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
