
<template>
  <div class="app-container">
  <h3>sql	</h3>
  <el-input v-model="sql" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" placeholder = "支持命名参数，占位符，例如：select * from user where id =:id" />
  
  <h3>参数</h3>
  <el-input v-model="param" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" placeholder="json，逗号分隔参数，mybatis 日志参数" />
  <br/><br/>
  <el-button type="primary" @click="execute">执行</el-button>
  <br/> <br/>
  <el-input v-model="result" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" />
  </div>
</template>

<script>
import { Message } from 'element-ui';
import service from '@/utils/request'
import { isBlank,isString } from '@/utils/validate'

export default {
  data() {
    return {
      sql:'',
      param:'',
      result:''
    }
  },
  methods: {
    execute() {
      var sql = this.sql;
      var param = this.param;
      if (isBlank(sql) || isBlank(param)) {
          Message.warning('sql或参数不能为空');
          return;
      }
      if (!sql.includes(":") && !sql.includes("?")) {
          Message.warning('sql必须包含冒号或者问号');
          return;
      }
      if (sql.includes(":") && sql.includes("?")) {
          Message.warning("sql不能同时包含冒号或者问号");
          return;
      }
      sql += " ";
      // 判断命名参数还是占位符
      var isMap = sql.includes(":");
      var data = null;
      try {
          // 将json字符串转换成json对象或者数组
          data = JSON.parse(param);
      } catch(error) {
          if (isMap) {
              Message.warning("参数格式有误，占位符赋值，必须是json对象格式");
              return;
          }
          data = param.split(",");
      }
      // 如果是命名参数
      if (isMap) {
          for (var key in data) {
              var value = data[key];
              if (isString(value)) {
                  value = "'" + value + "'";
              } else if (value instanceof Array) {
                  value = value.join(",");
              }
//              sql = sql.replace(new RegExp(":" + key + "(\\W)", "gm"), value+"$1");　
              sql = sql.replace(new RegExp(":" + key + "\\b", "gm"), value);　
          }
      } else {
          if (!data instanceof Array) {
              Message.warning("参数不是json数组");
              return;
          }
          var types = ["string", "datetime","timestamp","date"];
          data.forEach(function(item) {
            // mybatis 日志参数，会包含类型，例如：460(Long)
              if (isString(item) && item.includes("(") && item.includes(")")) {
                  var type = item.substring(item.lastIndexOf("(") + 1, item.lastIndexOf(")")); 
                  item = item.substring(0, item.lastIndexOf("("));
                  if (types.indexOf(type.toLowerCase()) != -1) {
                      item = "'" + item + "'";
                  }
              } else {
                  if ((item)) {
                      item = "'" + item + "'";
                  }
              }
              sql = sql.replace("?", item);
          });
      }
      if (sql.includes("?")) {
          Message.warning("有参数未赋值");
      }
      this.result = sql; 
    }
  }
}
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

