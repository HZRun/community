package com.gdufs.demo.intercetor;

import com.alibaba.fastjson.JSONObject;
import com.gdufs.demo.annotation.AdminAuthToken;
import com.gdufs.demo.annotation.AuthToken;
import com.gdufs.demo.utils.CacheUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @author cailei.lu
 * @description
 * @date 2018/8/3
 */

public class AuthorizationInterceptor implements HandlerInterceptor {

    //private static final Logger LOGGER = LoggerFactory.getLogger();


    //存放鉴权信息的Header名称，默认是Authorization
    private String httpHeaderName = "Authorization";

    //鉴权失败后返回的错误信息，默认为401 unauthorized
    private String unauthorizedErrorMessage = "401 unauthorized";

    //鉴权失败后返回的HTTP错误码，默认为401
    private int unauthorizedErrorCode = HttpServletResponse.SC_UNAUTHORIZED;

    /**
     * 存放登录用户模型Key的Request Key
     */
    public static final String REQUEST_CURRENT_KEY = "REQUEST_CURRENT_KEY";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 如果打上了AuthToken注解则需要验证token
        if (method.getAnnotation(AuthToken.class) != null || handlerMethod.getBeanType().getAnnotation(AuthToken.class) != null) {//普通用户认证
            String token = request.getHeader(httpHeaderName);
            //log.info("token is {}", token);
            String username = "";
            if (token != null && token.length() != 0) {
                if (CacheUtils.getString("token:web:" + token) != null || CacheUtils.getString("token:app:" + token) != null) { //token 未过期
                    request.setAttribute(REQUEST_CURRENT_KEY, username);
                    return true;
                } else {
                    System.out.println(token);
                    System.out.println("ffffalse");
                    JSONObject jsonObject = new JSONObject();
                    PrintWriter out = null;
                    try {
                        response.setStatus(unauthorizedErrorCode);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        jsonObject.put("code", ((HttpServletResponse) response).getStatus());
                        jsonObject.put("message", HttpStatus.UNAUTHORIZED);
                        out = response.getWriter();
                        out.println(jsonObject);
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null != out) {
                            out.flush();
                            out.close();
                        }
                    }
                }
                //log.info("username is {}", username);
            } else {
                JSONObject jsonObject = new JSONObject();
                PrintWriter out = null;
                try {
                    response.setStatus(unauthorizedErrorCode);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    jsonObject.put("code", ((HttpServletResponse) response).getStatus());
                    jsonObject.put("message", HttpStatus.UNAUTHORIZED);
                    out = response.getWriter();
                    out.println(jsonObject);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != out) {
                        out.flush();
                        out.close();
                    }
                }

            }
        } else if (method.getAnnotation(AdminAuthToken.class) != null || handlerMethod.getBeanType().getAnnotation(AdminAuthToken.class) != null) { //管理员认证
            String token = request.getHeader(httpHeaderName);
            System.out.println("进入token验证了2");
            //log.info("token is {}", token);
            String username = "";
            if (token != null && token.length() != 0) {
                if (CacheUtils.getString("token:web:" + token) != null) { //token 未过期
                    request.setAttribute(REQUEST_CURRENT_KEY, username);
                    return true;
                } else {
                    JSONObject jsonObject = new JSONObject();
                    PrintWriter out = null;
                    try {
                        response.setStatus(unauthorizedErrorCode);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        jsonObject.put("code", ((HttpServletResponse) response).getStatus());
                        jsonObject.put("message", HttpStatus.UNAUTHORIZED);
                        out = response.getWriter();
                        out.println(jsonObject);
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null != out) {
                            out.flush();
                            out.close();
                        }
                    }
                }
                //log.info("username is {}", username);
            } else {
                JSONObject jsonObject = new JSONObject();
                PrintWriter out = null;
                try {
                    response.setStatus(unauthorizedErrorCode);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    jsonObject.put("code", ((HttpServletResponse) response).getStatus());
                    jsonObject.put("message", HttpStatus.UNAUTHORIZED);
                    out = response.getWriter();
                    out.println(jsonObject);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != out) {
                        out.flush();
                        out.close();
                    }
                }

            }
        }
        request.setAttribute(REQUEST_CURRENT_KEY, null);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

