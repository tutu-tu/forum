package com.plantrice.forum.interceptor;

import com.plantrice.forum.annotation.LoginRequired;
import com.plantrice.forum.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

//必须登录才能访问的拦截器
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    //在请求最初，判断你是否进行了登录状态
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        //判断有没有登录，尝试获取当前的用户信息，
        //判断handler（拦截）是否是方法类型的
        if (handler instanceof HandlerMethod) {
            //进行转型，因为Object类型不方便获取里面的内容
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //直接获取到他拦截到的对象 ，有了method对象可以尝试从方法对象中去注解
            Method method = handlerMethod.getMethod();
            //按照置顶的类型去取
            //method.getDeclaredAnnotations() 取所有的类型
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //没有登录
            if (loginRequired != null && hostHolder.getUser() == null) {
                //重定向 request.getContextPath() 从请求中取到项目路径。
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
