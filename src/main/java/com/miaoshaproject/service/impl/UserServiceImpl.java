package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDo = userDOMapper.selectByPrimaryKey(id);

        if(userDo == null){
            return null;
        }
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDo.getId());

        return convertFromDataObject(userDo,userPasswordDO);
    }

    @Override
    @Transactional//添加事务
    public void register(UserModel userModel) throws BusinessException {

        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //此处使用apache.commons包中的StringUtils.isEmpty()相当于对String类型做非空判断
        if(StringUtils.isEmpty(userModel.getName()) || userModel.getAge() == null ||
                userModel.getGender() == null || StringUtils.isEmpty(userModel.getTelphone())
                || StringUtils.isEmpty(userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //因为怕userDOMapper表已经插入而userPasswordDOMapper没有插入，所以加入事务
        UserDO userDO = convertFromModel(userModel);
        //insertSelective比insert多出了每个入参的非空判断
        userDOMapper.insertSelective(userDO);
        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){

        if(userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){

        if(userModel == null){
            return  null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){

        if(userDO == null){
            return null;
        }

        UserModel userModel = new UserModel();
        //相当于把userDO的属性复制到userModel中
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
