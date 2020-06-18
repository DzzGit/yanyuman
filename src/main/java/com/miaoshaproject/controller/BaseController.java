package com.miaoshaproject.controller;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个异常类，可以使用到的地方还很多，所以把代码单独拿出来，其他地方用的到的可以直接继承这个类就可以正常使用
 * 这也是面向对象编程的特点
 */
public class BaseController {

    //定义ExceptionHandler解决未被controller层吸收的exception
    @ExceptionHandler(Exception.class)
    //定义ResponseStatus即便是页面500也可以正确的返回页面
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){

        Map<String,Object> map = new HashMap<>();
        if(ex instanceof BusinessException){
            BusinessException businessException = (BusinessException)ex;
            map.put("errCode",businessException.getErrCode());
            map.put("errMsg",businessException.getErrMsg());
        }else {
            map.put("errCode", EmBusinessError.UNKNOWN_ERROR.getErrCode());
            map.put("errMsg",EmBusinessError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(map,"fail");

    }

}
