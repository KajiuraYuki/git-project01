Controller部分代码：
package com.controller;

import com.entity.ResetPwdModel;
import com.mapper.ResetPwdMapper;
import com.service.ResetPwdService;
import com.utils.MyTimerTask;
import com.utils.Code;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;


/**
 * @Classname ResetPwdController
 * @Description 密码重置相关接口
 */
@RestController
public class ResetPwdController {

    Code getcode = new Code();

    /**
     * (重置密码邮件发送之前，所有验证通过)
     * 如果存在用户信息生成新的验证码，以及新的有效时间对原来的数据进行覆盖
     * 如果是第一次进行密码重置(即重置密码信息表中没有该用户信息)则将数据信息存入到重置密码信息表中
     **/
    @Autowired
    ResetPwdMapper resetPwdDAO;
    ResetPwdService resetPwdService;

    @CrossOrigin
    @PostMapping(value = "/api/resetpwd/addresetpwd", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResetPwdModel addResetpwd(@RequestBody Map<String, Object> json) {
        Timer timer = new Timer();
        Integer zhanghu = Integer.valueOf(String.valueOf(json.get("zhanghu")));
        ResetPwdModel resetPwdModel = resetPwdDAO.findByZhanghu(zhanghu);
        Integer type = 0;
        String typeStr = String.valueOf(json.get("type"));
        switch (typeStr) {
            case "admin":
                type = 1;
                break;
            case "teacher":
                type = 2;
                break;
            case "student":
                type = 3;
                break;
        }
        boolean flag = false;
        if (resetPwdModel == null) {
            resetPwdModel = new ResetPwdModel();
            resetPwdModel.setZhanghu(zhanghu);
            resetPwdModel.setType(type);
            flag = true;
        }
        resetPwdModel.setCode(getcode.getCode());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resetPwdModel.setS_time(df.format(new Date()));
        resetPwdModel.setState_code(0);
        if (flag) {
            resetPwdDAO.insert(resetPwdModel);
        } else {
            resetPwdDAO.updateById(resetPwdModel);
        }
        // 创建/更新验证码之后执行一个定时器，实现控制验证码的有效期
        MyTimerTask myTimerTask = new MyTimerTask(timer, zhanghu, type);
        timer.schedule(myTimerTask, 5000);
        return resetPwdModel;
    }


    /**
     * 重置密码完成，将原来的验证码进行覆盖，防止使用原来的验证码进行重置
     **/
    @CrossOrigin
    @PostMapping(value = "/api/resetpwd/clrcode{zhanghu}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object clearCode(@PathVariable("zhanghu") Integer zhanghu) {
        ResetPwdModel resetPwdModel = resetPwdDAO.findByZhanghu(zhanghu);
        resetPwdModel.setCode(getcode.getCode());
        resetPwdDAO.updateById(resetPwdModel);
        return resetPwdModel;
    }


    /**
     * 检查当前账户的状态码
     **/
    @CrossOrigin
    @PostMapping(value = "/api/resetpwd/checkstatus", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object checkStatus(@RequestBody Map<String, Object> json) {
        Integer zhanghu = Integer.valueOf(String.valueOf(json.get("zhanghu")));
        Integer type = Integer.valueOf(String.valueOf(json.get("typevalue")));
        String code = String.valueOf(json.get("code"));

        code=code.split("\r")[0];
        ResetPwdModel resetPwdModel = resetPwdDAO.findByZhanghuAndTypeAndCode(zhanghu, type, code);
        System.out.println(resetPwdModel.toString());
        if (resetPwdModel == null) {
            // 用户不存在，url链接错误
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "url有误！请输入正确的url地址！");
            jsonObject.put("code", "256");
            return jsonObject.toString();
        } else {
            if (resetPwdModel.getState_code()==1) {
                JSONObject jsonObject = new JSONObject();
                // 验证码在有效期之内
                jsonObject.put("msg", "url验证通过！");
                jsonObject.put("code", "200");
                return jsonObject.toString();
            } else {
                // 验证码已经失效
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "该链接已失效！请重新获取链接！");
                jsonObject.put("code", "256");
                return jsonObject.toString();
            }
        }
    }
}
Model部分代码：
package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @Classname ResetPwdModel
 * @Description 重置密码表模型
 */
@TableName(value = "reset_pwd_table")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class ResetPwdModel {
    //@TableId(type = IdType.AUTO)
    private Integer zhanghu; // 账号
    private Integer type;       // 账号类型
    private String code;    // 验证码
    private String s_time;  // 有效时间
    private Integer state_code; // 状态码

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getZhanghu() {
        return zhanghu;
    }

    public void setZhanghu(Integer zhanghu) {
        this.zhanghu = zhanghu;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }

    public Integer getState_code() {
        return state_code;
    }

    public void setState_code(Integer state_code) {
        this.state_code = state_code;
    }

    @Override
    public String toString() {
        return "ResetPwdModel{" +
                "zhanghu=" + zhanghu +
                ", type=" + type +
                ", code='" + code + '\'' +
                ", s_time='" + s_time + '\'' +
                ", state_code=" + state_code +
                '}';
    }
}
数据库中：/*
 Navicat Premium Data Transfer

 Source Server         : 1
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : studentdb

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 05/06/2023 15:54:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for reset_pwd_table
-- ----------------------------
DROP TABLE IF EXISTS `reset_pwd_table`;
CREATE TABLE `reset_pwd_table`  (
  `zhanghu` int(11) NOT NULL COMMENT '用户账户',
  `type` int(11) NOT NULL DEFAULT 0 COMMENT '1:管理员;2:教师;3:学生',
  `code` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `s_time` datetime(0) NOT NULL,
  `state_code` int(11) NOT NULL DEFAULT 1 COMMENT '0:已过期;1:未过期',
  PRIMARY KEY (`zhanghu`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reset_pwd_table
-- ----------------------------
INSERT INTO `reset_pwd_table` VALUES (10001, 1, 'r6eb', '2023-06-01 18:17:09', 1);
INSERT INTO `reset_pwd_table` VALUES (10002, 1, 'YdoS', '2023-06-01 20:18:00', 1);
INSERT INTO `reset_pwd_table` VALUES (20001, 2, 'nh48', '2023-06-05 14:56:25', 1);
INSERT INTO `reset_pwd_table` VALUES (20002, 2, 'icRJ', '2023-06-05 11:48:07', 1);
INSERT INTO `reset_pwd_table` VALUES (2014490001, 3, 'nEji', '2023-06-05 08:33:58', 1);
INSERT INTO `reset_pwd_table` VALUES (2014490003, 3, 'XS2d', '2023-06-01 20:17:20', 1);

SET FOREIGN_KEY_CHECKS = 1;
但是控制台输出ResetPwdModel{zhanghu=20001, type=2, code='4xwq', s_time='null', state_code=null}
，state_code的值为null，如何解决
