package com.miaoshaproject.error;

/**
 * 定义EmBusinessError枚举实现了统一管理错误码，后期再有添加可以直接在下面扩展
 */
public enum EmBusinessError implements CommonError {
    //这里定义的每一个枚举都是由errCode和errMsg组成，也就说下面的10001为errCode；而剩下的就是errMsg
    //通用错误类型10001
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),
    //20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001,"用户不存在")
    ;

    private EmBusinessError(int errCode,String errMsg){

        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    private int errCode;
    private String errMsg;

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
