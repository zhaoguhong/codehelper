
<template>
  <div class="app-container">
     <span id = "content" style="display:none">
### 字段

```sql
--添加
ALTER TABLE `user` ADD COLUMN `mobile`  varchar(20) DEFAULT NULL COMMENT '手机号码';

-- 修改 备注
ALTER TABLE `user` MODIFY COLUMN `mobile`  varchar(20) NOT NULL COMMENT '手机号码';

-- 修改字段名
ALTER TABLE `EF_PA` 
CHANGE COLUMN `CUSTOMER_ BALANCE` `CUSTOMER_ BALANCE` DECIMAL (18, 5) DEFAULT NULL COMMENT '客户余额';

--删除
ALTER TABLE `user` DROP COLUMN `mobile`;
```

### 表

```sql
-- 删除
DROP TABLE IF EXISTS `demo`;

-- 清空表数据，保留表结构
TRUNCATE TABLE `demo`;

-- 创建表
CREATE TABLE `demo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `REMARK` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试';

```

### 索引

```sql
-- 添加
ALTER TABLE `demo` ADD INDEX index_demoId (`demo_id`);
```


     
     </span>
    <div id="main">
      <mavon-editor v-model="value" :editable='editable' 
        :toolbarsFlag='toolbarsFlag' 
        :subfield='subfield' 
        :defaultOpen = 'defaultOpen' ></mavon-editor>
    </div>
    </div>
</template>

<script>

export default {
  data() {
    return { 
        value:"",
        subfield: false,// 单双栏模式
        defaultOpen: 'preview',//edit： 默认展示编辑区域 ， preview： 默认展示预览区域 
        editable: true,
        toolbarsFlag: false,
        scrollStyle: true
    }
  },
  mounted: function(){
    this.value = document.getElementById("content").innerText;
  }
  }
</script>

