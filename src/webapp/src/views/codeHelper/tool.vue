
<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="12">
      <el-input v-model="oldVal" type="textarea" :rows="33"/>
      </el-col>
      <el-col :span="12">
      <el-input v-model="newVal" type="textarea" :rows="33" />
      </el-col>
    </el-row>
    <div align="center">
      <el-row >
        <el-button type="primary" @click="extractSql">提取sql</el-button>
        <el-button type="primary" @click="stringBuilder">拼接sql</el-button>
        <el-button type="primary" @click="getSetMethod">getSetMethod</el-button>
        <el-button type="primary" @click="jsonToJava">jsonToJava</el-button>
        <el-button type="success" @click="jsFormat">js/json格式化</el-button>
        <el-button type="success" @click="htmlFormat">html格式化</el-button>
        <el-button type="success" @click="sqlFormat">sql格式化</el-button>
        <el-button type="warning" @click="clear">clear</el-button>
      </el-row>
    </div>
   
  </div>
</template>

<style>
  .el-row {
    margin-bottom: 20px;
  }
</style>

<script>
import { isBlank,isNotBlank,isString } from '@/utils/validate'
import { getNextWord,isJSON,isArray } from '@/utils/common'
import { js_beautify } from '@/utils/jsformat'
import { style_html } from '@/utils/htmlformat'
import axios from '@/utils/request'

export default {
  data() {
    return { 
        oldVal : "",
				newVal : ""
    }
  },
  methods : {
				// 清屏
				clear : function() {
					this.oldVal = "";
					this.newVal = "";
				},
				// 提取sql
				extractSql : function() {
					var sql=this.oldVal.match(/\"(.*?)\"/g).join("").replace(/"/g,"");
          axios.post('/formatSql', {
						sql : sql
					})
          .then(data => {
            this.newVal = data;
          })
				},
				// 转stringBuilder
				stringBuilder : function() {
					var arr = this.oldVal.split("\n");
					this.newVal = "StringBuilder sql = new StringBuilder();\n";
					for (var i = 0; i < arr.length; i++) {
						if (isNotBlank(arr[i])) {
							this.newVal += "sql.append(\" " + arr[i]
									+ " \");\n";
						}
					}
				},
				// 获取set方法
				getSetMethod : function() {
					var oldStrArray = this.oldVal.split("\n");
					var objectName = "";
					var className = "";
					this.newVal = "";
					for (var i = 0; i < oldStrArray.length; i++) {
						if (oldStrArray[i].indexOf(" class ") != -1) {
							className = getNextWord(oldStrArray[i], "class");
							objectName = className.substring(0, 1)
									.toLowerCase()
									+ className.substring(1);
						}
						if (oldStrArray[i].indexOf("private ") != -1) {
							var type = getNextWord(oldStrArray[i], "private");
							var filed = getNextWord(oldStrArray[i], type);
							this.newVal += objectName + ".set"
									+ filed.substring(0, 1).toUpperCase()
									+ filed.substring(1) + "(" + filed + ");\n";
						}
					}
					var newObj = className + " " + objectName + " = new "
							+ className + "();\n";
					this.newVal = newObj + this.newVal;
				},
				// js 格式化
				jsFormat : function() {
					if (isNotBlank(this.oldVal)) {
						this.newVal = js_beautify(this.oldVal);
					}
				},
				// html 格式化
				htmlFormat : function() {
					if (isNotBlank(this.oldVal)) {
						this.newVal = style_html(this.oldVal);
					}
				},
				// sql 格式化
				sqlFormat : function() {
					if (isNotBlank(this.oldVal)) {
						 axios.post('/formatSql', {
              sql : sql
            })
            .then(data => {
              this.newVal = data;
            })
					}
				},
        // json 转 Java
        jsonToJava: function() {
            if (!isJSON(this.oldVal)) {
                layer.msg("参数格式有误，必须是json格式");
                return;
            }
            var javaStr = "";
            var getObjStr = function(name, obj) {
                javaStr += "\n";
                javaStr += "Map<String,Object> " + name + " = Maps.newHashMap();";
                var putMap = function(key, value) {
                    javaStr += name + '.put("' + key + '",' + value + ');';
                }
                for (var key in obj) {
                    var value = obj[key];
                    if (typeof value == "object") {
                        if (isArray(value)) {
                            getObjArray(key, value);
                            putMap(key, key);
                        } else {
                            getObjStr(key, value);
                            putMap(key, key);
                        }
                    } else if (isString(value)) {
                        putMap(key, '"' + value + '"');
                    } else {
                        putMap(key, value);
                    }
                }
            };

            var getObjArray = function(name, array) {
                javaStr += "\n";
                javaStr += "List<Object> " + name + " = Lists.newArrayList();";
                var i = 1;
                array.forEach(function(item) {
                    if (typeof item == "object") {
                        var separate = "Map";
                        if (isArray(item)) {
                            separate = "List";
                        }
                        var mapName = name + separate;
                        if (array.length != 1) {
                            mapName += i++;
                        }
                        if (isArray(item)) {
                            getObjArray(mapName, item);
                        } else {
                            getObjStr(mapName, item);
                        }
                        javaStr += name + ".add(" + mapName + ");";

                    } else if (isString(value)) {
                        javaStr += name + '.add("' + item + '");';
                    } else {
                        javaStr += name + ".add(" + item + ");";
                    }
                });
            };

            var str = JSON.parse(this.oldVal);
            if (isArray(str)) {
                getObjArray("list", str);
            } else {
                getObjStr("map", str);
            }
            this.newVal = javaStr.replace(/;/g, ";\n");
        },
				// 获取查询SQL
				getSelectSql : function() {
					if (isNotBlank(this.oldVal)) {
						request
								.getString(
										"/getSelectSql",
										{
											tableName : this.oldVal.replace(/'/g,""),
											dataSourceName : $(
													'.radio-inline input[name="optionsRadiosinline"]:checked ')
													.val()
										});
					}
				}
			}
  }
</script>

