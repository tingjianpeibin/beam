package com.ksh.beam.interceptors;
import com.ksh.beam.common.annotion.IgnoreUTokenAuth;
import com.ksh.beam.common.enumeration.RetEnum;
import com.ksh.beam.common.utils.R;
import com.ksh.beam.common.utils.RedisUtil;
import com.ksh.beam.common.utils.RenderUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 小程序拦截器
 */
@Component
public class AppInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(AppInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 执行完控制器后调用，即离开时
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception e) throws Exception {
        logger.info("className--->" + arg2);
        logger.info("request--->" + request);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String referer = request.getHeader("Referer");
        if(!org.springframework.util.StringUtils.isEmpty(referer) && (referer.contains("swagger") || referer.contains("apiDoc"))){
            return true;
        }
        if(request.getServletPath().contains("swagger") || request.getServletPath().contains("api-docs") || request.getServletPath().contains("webjars") || request.getServletPath().contains("configuration")){
            return true;
        }


        IgnoreUTokenAuth annotation;
        if(handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(IgnoreUTokenAuth.class);
        }else{
            return true;
        }
        //如果有@IgnoreUTokenAuth注解，则不验证utoken
        if(annotation != null){
            logger.info("utoken pass： {}", ((HandlerMethod) handler).getMethod());
            return true;
        }
        String utoken = request.getHeader("utoken");
        if (StringUtils.isBlank(utoken)){
            RenderUtil.renderJson(response, R.fail(RetEnum.TOKEN_ERROR.getRet(), RetEnum.TOKEN_ERROR.getMsg()));
            return false;
        }
        String uid = (String) redisUtil.get(utoken);
        if (uid == null){
            RenderUtil.renderJson(response, R.fail(RetEnum.TOKEN_EXPIRED.getRet(), RetEnum.TOKEN_EXPIRED.getMsg()));
            return false;
        }
        request.setAttribute("uid", uid);
        return true;
    }

}
