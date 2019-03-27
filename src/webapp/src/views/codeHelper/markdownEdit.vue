
<template>
  <div class="app-container">
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
        subfield: true,// 单双栏模式
        defaultOpen: 'preview',//edit： 默认展示编辑区域 ， preview： 默认展示预览区域 
        editable: true,
        toolbarsFlag: true,
        scrollStyle: true
    }
  }
  }
</script>

