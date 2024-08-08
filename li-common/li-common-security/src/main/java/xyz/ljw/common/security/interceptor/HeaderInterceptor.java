package xyz.ljw.common.security.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class HeaderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("进入拦截器！！！！！");
        response.setContentType("application/text; charset=UTF-8");
        response.getWriter().write("测试乱码".toCharArray());
//        return false;
        return true; // 返回 true 表示继续执行下一个拦截器或处理器
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        System.out.println("拦截器后置处理！！！！！！");
    }
}
