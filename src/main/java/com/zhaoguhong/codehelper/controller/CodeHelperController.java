package com.zhaoguhong.codehelper.controller;


import com.zhaoguhong.codehelper.service.CodeHelperService;
import com.zhaoguhong.codehelper.util.SQLFormatter;
import com.zhaoguhong.codehelper.vo.EntityConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoguhong on 2019/1/12.
 */
@RestController
@Slf4j
public class CodeHelperController {
    @Resource
    private CodeHelperService codeHelperService;

    @RequestMapping("/test")
    public Map<String, Object> test() {
        Map<String, Object> result = new HashMap<>();
        result.put("test", "ddd");
        System.out.print(result);
        return result;
    }


    /**
     * 根据表名获得实体类
     */
    @RequestMapping("/getEntity")
    @Transactional
    public String getEntity(@RequestBody EntityConfigVO config) {
        log.info("生成实体类：" + config);
        return codeHelperService.generateEntiy(config);
    }

    /**
     * 格式化SQL
     */
    @RequestMapping(value = "/formatSql", produces = "text/plain;charset=UTF-8")
    @Transactional
    public String formatSql(@RequestBody Map<String,Object> params) {
        String sql = MapUtils.getString(params,"sql");
        log.info("格式化SQL：" + sql);
        sql = new SQLFormatter().format(sql);
        if (sql.startsWith("\n")) {
            sql = sql.substring(1);
        }
        return sql;
    }


}
