<template>
  <div class="registerWrapper" id="registerBackground">
    <div class="formWrapper">
      <h1 class="registerTitle">{{ registerTitle }}</h1>
      <p class="registerSystem">{{ registerSystem }}</p>
      <el-form
        :model="ruleForm"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="demo-ruleForm"
        hide-required-asterisk
      >
        <el-form-item prop="userName">
          <el-input
            prefix-icon="el-icon-user"
            v-model="ruleForm.userName"
            placeholder="用户名"
          ></el-input>
        </el-form-item>
        <el-form-item prop="telephone">
          <el-input
            prefix-icon="el-icon-mobile-phone"
            v-model="ruleForm.telephone"
            placeholder="手机号"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            prefix-icon="el-icon-lock"
            v-model="ruleForm.password"
            placeholder="密码"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item class="registerButtonWrapper">
          <el-button
            class="registerButton"
            type="primary"
            @click="submitForm('ruleForm')"
            >注册</el-button
          >
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { addUser } from '@/request/user.js'


export default {
  name: 'Register',
  data() {
    return {
      registerTitle: '注册',
      registerSystem: '网盘',
      ruleForm: {
        telephone: '',
        userName: '',
        password: ''
      },
      rules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {
            min: 5,
            max: 20,
            message: '长度在 5 到 20 个字符',
            trigger: 'blur'
          }
        ],
        telephone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { min: 11, max: 11, message: '请输入11位手机号', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    url() {
      let _url = this.$route.query.Rurl //获取路由前置守卫中next函数的参数，即登录后要去的页面
      if (_url) {
        //若登录之前有页面，则登录后仍然进入该页面
        return _url
      } else {
        //若登录之前无页面，则登录后进入首页
        return '/'
      }
    }
  },
  watch: {
  },
  created() {
  },
  methods: {
    //  注册按钮
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          //  各项校验通过
          let info = {
            telephone: this.ruleForm.telephone,
            username: this.ruleForm.userName,
            password: this.ruleForm.password,
          }
          addUser(info).then(res => {
            if (res.success == false) {
              this.$message.error(res.errorMessage)
            } else {
              //	这里的返回字段需要和后台重新商议
              this.$notify({
                title: '成功',
                message: '注册成功！已跳转到登录页面',
                type: 'success'
              })
              this.$refs[formName].resetFields();
              this.$router.replace({ path: '/login' })
            }
          })
        } else {
          this.$message.error('请完善信息！')
          return false
        }
      })
    }
  }
}
</script>
<style lang="stylus" scoped>
.registerWrapper
  height: 500px !important
  min-height: 500px !important
  width: 100% !important
  padding-top: 50px
  .formWrapper
    width: 375px
    margin: 0 auto
    text-align: center
    .registerTitle
      margin-bottom: 10px
      font-weight: 300
      font-size: 30px
      color: #000
    .registerSystem
      font-weight: 300
      color: #999
    .demo-ruleForm
      width: 100%
      margin-top: 20px
      >>> .el-form-item__content
        margin-left: 0 !important
      &>>> .el-input__inner
        font-size: 16px
      .registerButtonWrapper
        .registerButton
          width: 100%
        &>>> .el-button
          padding: 10px 90px
          font-size: 16px
    .tip
      width: 70%
      margin-left: 86px
</style>
