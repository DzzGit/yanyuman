package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin //此注解可以解决springboot ajax跨域问题
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/index")
    public String Index(){
        return "getOtp";
    }

    @RequestMapping(value = "/register",method = {RequestMethod.POST})
    @ResponseBody
    //用户注册接口
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "password")String password,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpCode相符合
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);

        //此处之所以使用类库中的equals，是因为其中已经做了传入值的非空判断，减少自己的代码量
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合 ");
        }

        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setThirdPartyId("");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newStr;
    }

    @RequestMapping("/test")
    public String home(){
        return "list";
    }

    //用户获取otp短信接口
    @RequestMapping(value = "/getOtp",method = {RequestMethod.POST})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone){
        //需要按照一定规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        System.out.println(randomInt);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        //将otp验证码同对应用户的手机号关联,目前通过httpSession的方式绑定他的手机号y与OTPCODE
        httpServletRequest.getSession().setAttribute(telphone,otpCode);
//        Object a  = httpServletRequest.getSession().getAttribute("13675311890");
//        String b = String.valueOf(a);
//        System.out.print(b);
        //将otp验证码通过短信方式发送给用户(//省略)
        System.out.println("telphone=" + telphone + "&optCode=" + otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);

        //若获取的对应用户信息不存在
        if(userModel == null){
            //userModel.setEncrptPassword("123");
            //因为EmBusinessError实现了CommonError所以在构造方法里可以这么写,并且BusinessException继承了Exception所以可以在这里抛出异常
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){

        if(userModel == null){
            return null;
        }else {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userModel,userVO);
            return userVO;
        }
    }

}
