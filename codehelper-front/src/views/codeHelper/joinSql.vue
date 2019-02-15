
<template>
  <div class="app-container">
</el-input>
    <el-form ref="form" :model="form" label-width="80px">
    <h3>基本配置</h3>
    <el-row>
        <el-form-item label="初始化sql">
            <el-input v-model="initSql" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" placeholder = "初始化sql,可以为多行" />
        </el-form-item>
    </el-row>
       
    
        <el-row>
          <el-col :span="6">
              <el-form-item label="框架">
                <el-select v-model="frame">
                    <el-option  v-for="option in frameOptions" v-bind:label="option" v-bind:value="option"/>
                </el-select>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="赋值方式">
                <el-select v-model="assignmentType">
                    <el-option  v-for="option in assignmentTypeOptions" v-bind:label="option.label" v-bind:value="option.value"/>
                </el-select>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="mapperId">
                <el-input v-model="mapperId"/>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="返回类型">
                <el-input v-model="returnType"/>
              </el-form-item>
          </el-col>
        </el-row>
        <h3>参数配置</h3>

       <el-table
      :data="parameters"
      style="width: 100%">
      <el-table-column
        prop="name"
        label="参数名称">
         <template slot-scope="scope">
            <el-input v-model="scope.row.name" placeholder="请填写名称" @input ="changeAppendSql(scope.$index)"></el-input>
          </template>
      </el-table-column>
      <el-table-column
        prop="dataType"
        label="参数类型">
         <template slot-scope="scope">
            <el-select v-model="scope.row.dataType" placeholder="参数类型" v-on:change="changeDataType(scope.$index)">
                <el-option v-for="option in dataTypeOptions" v-bind:label="option" v-bind:value="option"/>
            </el-select>
          </template>

      </el-table-column>

      <el-table-column
        prop="matchType"
        label="比较方式">
        <template slot-scope="scope">
            <el-select  v-if="scope.row.dataType == 'String'" v-model="scope.row.matchType">
                <el-option v-for="option in matchTypeOptions" 
                    v-bind:label="option" v-bind:value="option"/>
            </el-select>
            <el-select v-else v-model="scope.row.matchType">
                <el-option   v-for="option in matchTypeNumberOptions" 
                    v-bind:label="option" v-bind:value="option"/>
            </el-select>
        </template>
      </el-table-column>

       <el-table-column
        prop="verifyEmpty"
        label="判空方式"
        >

        <template slot-scope="scope">
            <el-select  v-if="scope.row.dataType == 'String'" v-model="scope.row.verifyEmpty">
                <el-option v-for="option in verifyEmptyOptions" 
                    v-bind:label="option.label" v-bind:value="option.value"/>
            </el-select>
            <el-select v-else v-model="scope.row.verifyEmpty">
                <el-option v-for="option in verifyEmptyNumberOptions" 
                    v-bind:label="option.label" v-bind:value="option.value"/>
            </el-select>
        </template>
      </el-table-column>
      <el-table-column
        prop="appendSql"
        label="appendSql"
        >
      </el-table-column>
      <el-table-column
        prop=""
        label="操作">
         <template slot-scope="scope">
        <el-button
          size="mini"
          type="danger"
          @click="del(scope.$index, scope.row)">删除</el-button>
          <el-button
          size="mini"
          type="success"
          @click="copy(scope.$index, scope.row)">复制</el-button>
          <el-button
          size="mini"
          type="primary"
          @click="add(scope.$index, scope.row)">添加</el-button>
      </template>
      </el-table-column>
    
    </el-table>
    <div style="margin-top: 20px;">
        <el-button type="primary" @click="reset()">重置</el-button>
            <el-button type="primary" @click="joinSql()">生成</el-button>

    </div>
      </el-form>
     <div style="margin-top: 20px;">
       <el-input v-show="showResult" v-model="sql" type="textarea" :autosize="{ minRows: 4, maxRows: 6}"/>
    </div>
        


    </div>
</template>

<script>
import { Message } from 'element-ui';
import service from '@/utils/request'
import { isBlank,isNotBlank,isString } from '@/utils/validate'
import { ArrayUtils } from '@/utils/common'


// 初始化默认参数配置
var initParameter = {
    "name": "",
    "appendSql": "",
    "dataType": 'String',
    "matchType": '=',
    "verifyEmpty": 1,
};

/**
 * 获取判断条件
 * @param    {string}  frame	框架
 * @param    {number}   verifyEmptyType  判空类型
 * @param    {string}  name   字段名称
 */
function getVerify(frame, verifyEmptyType, name) {
    let result = "";
    if (frame == "springjdbc" || frame == "hibernate") {
        switch (verifyEmptyType) {
        case 1:
            result = "StringUtils.isBlank(param)";break;
        case 2:
            result = "StringUtils.isEmpty(param)";break;
        case 3:
            result = "param != null";break;
        case 4:
            result = "param != null && param > 0";break;
        default:
            result = "";
        }
        result = "if(" + result + ") {";
    } else if (frame == "mybatis") {
        switch (verifyEmptyType) {
        case 1:
            result = "'param != null and .trim() != \"\"'";break;
        case 2:
            result = "param != null and param !=''";break;
        case 3:
            result = "param != null";break;
        case 4:
            result = "param != null and param > 0";break;
        default:
            result = "";
        }
        if (result.indexOf("'") != 0) {
            result = '"' + result + '"';
        }
        result = "\t<if test=" + result + ">";
    }
    return result.replace(/param/g, name);
}
// 拼接sql
var doJoinSql = function(config) {
    let frame = config.frame;
    const isMybatis = (frame == "mybatis");
    let assignmentType = config.assignmentType;
    let initSql = config.initSql;
    let returnType = config.returnType.trim();
    let mapperId = config.mapperId.trim();
    let errorMsg = "";
    let sqlName = frame == "springjdbc" ? "sql": "hql"; // sql变量名字
    let nameArray = []; // name数组集
    let sql = [];
    if (isMybatis) {
        sql.push("<select id=\"" + mapperId + "\" parameterType=\"java.util.Map\" resultType=\"" + returnType + "\">");
    } else {
        sql.push("StringBuilder " + sqlName + " = new StringBuilder();");
        if (assignmentType == 1) {
            sql.push("Map<String, Object> parameters = new HashMap<String, Object>();");
        } else {
            sql.push("List<Object> parameters = new ArrayList<Object>();)");
        }
    }
    var sqlArray = initSql.split("\n");
    sqlArray.forEach(function(e) {
        if (isNotBlank(e)) {
            if (isMybatis) {
                sql.push("\t" + e);
            } else {
                sql.push(sqlName + '.append(" ' + e + ' ");');
            }
        }
    });
    config.parameters.forEach(function(parameter, index) {
        var name = parameter.name.trim();
        var dataType = parameter.dataType;
        var verifyEmpty = parameter.verifyEmpty;
        var appendSql = parameter.appendSql.trim();
        var matchType = parameter.matchType;
        nameArray.push(name);
        if(errorMsg){
            return;
        }
        if (!name) {
            errorMsg = "名称不能为空";
        }else if (ArrayUtils.isRepeat(nameArray)) {
            errorMsg = "名称重复";
        } else if (assignmentType == 1 && appendSql.indexOf("?") > -1) {
            errorMsg = "赋值方式为命名参数，appendSql不允许包含字符 ? ";
        } else if (assignmentType == 2 && appendSql.indexOf(":") > -1) {
            errorMsg = "赋值方式为占位符，appendSql不允许包含字符 : ";
        }
        if (errorMsg) {
            errorMsg += ",第" + (index + 1) + "条数据不符合规则";
        }
        if (!isMybatis) {
            // 获取变量值
            sql.push(dataType + " " + name + " = MapUtils.get" + dataType + "(params, \"" + name + "\");");
        }
        // 变量判断是否为空
        sql.push(getVerify(frame, verifyEmpty, name));
        if (isMybatis) {
            sql.push("\t\t" + appendSql);
        } else if (frame == "springjdbc") {
            sql.push("  sql.append(\" " + appendSql + " \");");
        } else {
            sql.push("  hql.append(\" " + appendSql + " \");");
        }
        if (isMybatis) {
       	 sql.push("\t</if>");
       }else{
            let likeName = matchType == 'like' ? "Common.ConvertLikeParamer(" + name + ")": name;
            // 参数赋值
            if (assignmentType == 1) {
                sql.push("  parameters.put(\"" + name + "\", " + likeName + ");");
            } else {
                sql.push("  parameters.add(" + likeName + ");");
            }
            sql.push("}");
        }

    });
    if (errorMsg) {
        Message.warning(errorMsg);
        return errorMsg;
    }
    let resultSql = "";
    if (isMybatis) {
    	resultSql +="</select>";
    }else{
        // 执行sql 返回结果
    	resultSql +="List<" + returnType + "> lists = ";
        if (frame == "springjdbc" && assignmentType == 1) {
        	resultSql +="this.getNamedParameterJdbcTemplate().queryForList(" + sqlName + ".toString(), parameters);";
        } else if (frame == "springjdbc" && assignmentType == 2) {
        	resultSql +="this.getJdbcTemplate().queryForList(" + sqlName + ".toString(), parameters.toArray());";
        } else if (frame == "hibernate" && assignmentType == 1) {
            resultSql +="hdao.find(" + sqlName + ".toString(), parameters);";
        } else if (frame == "hibernate" && assignmentType == 2) {
        	resultSql +="hdao.find(" + sqlName + ".toString(), parameters.toArray());";
        }	
    }
    sql.push(resultSql);
    return sql.join("\n")
}

export default {
  data() {
    return {
        assignmentType: 1,
        assignmentTypeOptions: [{
                label: '命名参数',
                value: 1
            },
            {
                label: '占位符',
                value: 2
            }],
        frame: 'mybatis',
        frameOptions: ['mybatis','springjdbc','hibernate'],
        initSql: "",
        mapperId: "",
        returnType: "Object",
        parameters: [],
        showResult: false,
        sql: '',
        dataTypeOptions: ['String','Long','Integer'],
        matchTypeOptions: ['=','like','!='],
        matchTypeNumberOptions: ['=','!=','>','>=','<','<='],
        verifyEmptyOptions: [{
            label: '不为空白',
            value: 1
        },
        {
            label: '不为空',
            value: 2
        },
        {
            label: '不为null',
            value: 3
        }],
        verifyEmptyNumberOptions: [{
            label: '不为null并且大于0',
            value: 4
        },
        {
            label: '不为null',
            value: 3
        }]
    }
  },
  methods: {
        changeDataType: function(index) {
            var row = this.parameters[index];
            var dataType = row.dataType;
            var matchType = row.matchType;
            var verifyEmpty = row.verifyEmpty;
            if(dataType == 'String' && !this.matchTypeOptions.includes(matchType)){
                row.matchType = '=';
            }
            if(dataType != 'String' && !this.matchTypeNumberOptions.includes(matchType)){
                row.matchType = '=';
            }
            if(dataType == 'String' && !this.verifyEmptyOptions.includes(matchType)){
                row.verifyEmpty = 1;
            }
            if(dataType != 'String' && !this.verifyEmptyNumberOptions.includes(matchType)){
                row.verifyEmpty = 3;
            }
        },
        changeAppendSql: function(index) {
            var entity = this.parameters[index];
        	let name = entity.name.trim();
        	let assignmentType = this.assignmentType;
        	var matchType = entity.matchType;
            let isMybatis = (this.frame == "mybatis");
            let appendSql = "";
            if(!name){
                entity.appendSql = '';
                return;
            }
            if(isMybatis){
            	if(matchType == "like"){
            		appendSql = "CONCAT('%',#{" + name+ "},'%')";
            	}else{
            		appendSql = "#{" + name+ "}";
            	}
            }else if(assignmentType == 1){
            	appendSql = ":" + name;
            }else {
            	appendSql = "?";
            }
            entity.appendSql = "AND " + name + " " + matchType + " " + appendSql;
        },
        del: function(index) {
            if (this.parameters.length == 1) {
                Message.warning('参数至少要有一个');
            } else {
                this.parameters.splice(index, 1);
            }
        },
        add: function() {
            this.parameters.push(Object.assign({}, initParameter));
        },
        copy: function(index) {
            this.parameters.push(Object.assign({}, this.parameters[index]));
        },
        reset: function() {
            this.parameters = [];
            this.parameters.push(Object.assign({}, initParameter));
            this.showResult = false;
        },
        joinSql: function() {
            this.showResult = true;
            this.sql = doJoinSql(this);
        }
  },
  created: function(){
      // 初始化第一条数据
      this.parameters.push(Object.assign({}, initParameter));
  }
}

</script>

<style scoped>
.line{
  text-align: center;
}
</style>

