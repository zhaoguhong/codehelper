package com.zhaoguhong.codehelper.controller;


import com.zhaoguhong.codehelper.vo.EntityConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoguhong on 2019/1/12.
 */
@RestController
@Slf4j
public class CodeHelperController {

    @RequestMapping("/test")
    public Map<String,Object> test(){
        Map<String,Object> result = new HashMap<>();
        result.put("test","ddd");
        System.out.print(result);
        return  result;
    }



    /**
     * 根据表名获得实体类
     *
     */
    @RequestMapping("/getEntity")
    public String getEntity(@RequestBody EntityConfigVO config) {
        log.info("生成实体类：" + config);
        return null;
    }



}
