package com.gdufs.demo.handler;

import com.gdufs.demo.entity.ResultBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value = InputErrorException.class)
    @ResponseBody
    private Map<String, Object> exceptionHandler(HttpServletRequest req, InputErrorException e) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("status", e.getCode());
        modelMap.put("msg", e.getMsg());
        return modelMap;
    }

    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    private Map<String, Object> runtimeExceptionHandler(HttpServletRequest req, RuntimeException e) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("status", 500);
        modelMap.put("msg", e.getMessage());
        return modelMap;
    }

    @ExceptionHandler(value = PermissionException.class)
    @ResponseBody
    private Map<String, Object> permissionExceptionHandler(HttpServletRequest req, PermissionException e) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("status", e.getCode());
        modelMap.put("msg", e.getMsg());
        return modelMap;
    }

    @ExceptionHandler(value = StatusErrorException.class)
    @ResponseBody
    private Map<String, Object> statusErrorExceptionHandler(HttpServletRequest req, StatusErrorException e) {
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("status", e.getCode());
        modelMap.put("msg", e.getMsg());
        return modelMap;
    }

}
