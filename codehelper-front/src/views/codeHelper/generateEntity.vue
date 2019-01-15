<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="100px">
      <el-row>
          <el-col :span="6">
              <el-form-item label="数据库" required='true'>
                  <el-input v-model="form.databaseName"/>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="表名" required='true'>
                  <el-input v-model="form.tableName"/>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="继承类名">
                  <el-input v-model="form.extendClass"/>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="包路径">
                  <el-input v-model="form.packagePath"/>
              </el-form-item>
          </el-col>
      </el-row>
      <el-form-item label="忽略字段">
        <el-input v-model="form.ignoreFields" placeholder="多个用逗号分隔"/>
      </el-form-item>

      <el-row>
          <el-col :span="6">
              <el-form-item label="jpa">
                <el-switch v-model="form.jpa.enable"/>
              </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="Column">
                  <el-select v-model="form.jpa.column" :disabled='!form.jpa.enable' >
                    <el-option label="字段不相同时添加" :value=1 />
                    <el-option label="不符合驼峰时添加" value=2 />
                    <el-option label="全部添加" value=3 />
                  </el-select>
                </el-form-item>
          </el-col>
          <el-col :span="6">
              <el-form-item label="注解位置">
                  <el-radio-group v-model="form.jpa.inMethod" :disabled='!form.jpa.enable'>
                    <el-radio :label='true' >方法</el-radio>
                    <el-radio :label='false' >属性</el-radio>
                  </el-radio-group>
              </el-form-item>
          </el-col>
          
          <el-col :span="6">
              <el-form-item label="主键策略"> 
                  <el-select v-model="form.jpa.primaryKeyStrategy" :disabled='!form.jpa.enable' placeholder='默认' clearable>
                    <el-option label="AUTO" value="AUTO"/>
                    <el-option label="IDENTITY" value="IDENTITY"/>
                    <el-option label="SEQUENCE" value="SEQUENCE"/>
                    <el-option label="TABLE" value="TABLE"/>
                  </el-select>
                </el-form-item>
          </el-col>
      </el-row>
      <el-row> 
          <el-col :span="6">
              <el-form-item label="lombok">
                <el-switch v-model="form.lombok.enable"/>
              </el-form-item>
          </el-col>
          <el-col :span="18">
            <el-form-item label="注解">
                <el-checkbox v-model="form.lombok.Data" :disabled='!form.lombok.enable'>Data</el-checkbox>
                  <el-checkbox v-model="form.lombok.Builder" :disabled='!form.lombok.enable'>Builder</el-checkbox>
                  <el-checkbox v-model="form.lombok.AllArgsConstructor" :disabled='!form.lombok.enable'>AllArgsConstructor</el-checkbox>
                  <el-checkbox v-model="form.lombok.NoArgsConstructor" :disabled='!form.lombok.enable'>NoArgsConstructor</el-checkbox>
                  <el-checkbox v-model="form.lombok.Getter" :disabled='!form.lombok.enable'>Getter</el-checkbox>
                  <el-checkbox v-model="form.lombok.Setter" :disabled='!form.lombok.enable'>Setter</el-checkbox>
                </el-form-item> 
          </el-col>
      </el-row>
      

      <el-row>
          <el-col :span="24">
              <el-form-item label="注释">
                <el-checkbox v-model="form.fieldAnnotation">类注释</el-checkbox>
                <el-checkbox v-model="form.classAnnotation">属性注释</el-checkbox>
                <el-checkbox v-model="form.doradoAnnotation">dorado注释</el-checkbox>
              </el-form-item>
          </el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="onSubmit">生成</el-button>
        <el-button @click="onCancel">重置</el-button>
      </el-form-item>
    </el-form>
    <el-input v-model="content" v-if="content" type="textarea" label-width="120px"/>
  </div>
</template>

<script>

import axios from '@/utils/request'

export default {
  data() {
    return {
      form: {
        databaseName: '',
        extendClass: '',
        packagePath: '',
        fieldAnnotation: true,
        classAnnotation: true,
        doradoAnnotation: false,
        ignoreFields: '',
        jpa: {
          enable: true,
          inMethod: true,
          column: 1,
          primaryKeyStrategy:''
        },
        lombok: {
          enable:false,
          Data: true,
          Builder: true,
          AllArgsConstructor: true,
          NoArgsConstructor: true,
          Getter: false,
          Setter: false
        },
        lombokConfig:''
      },
      content:''
    }
  },
  methods: {
    onSubmit() {
      var jpa = this.form.jpa;
      let lombokConfig = [];
      if(jpa.enable){
        for(let key in jpa){
          if(jpa[key]){
            lombokConfig.push(key);
          }
        }
      }
      this.form.lombokConfig = lombokConfig.join(",");
      axios.post('/getEntity', this.form)
      .then(function (response) {
        console.log(response);
      })
      .catch(function (error) {
        console.log(error);
      });
    },
    onCancel() {
      this.$message({
        message: 'cancel!',
        type: 'warning'
      })
    }
  }
}
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

