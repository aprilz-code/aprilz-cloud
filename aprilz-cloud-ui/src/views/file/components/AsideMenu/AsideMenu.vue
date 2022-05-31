<template>
  <div class="aside-menu-wrapper expand">
    <!-- 分类栏 -->
    <el-menu
        class="el-menu-vertical-demo"
      :default-openeds="[0]"
      :default-active="activeIndex"
      @select="handleSelect"
    >
      <el-menu-item index="0">
        <i class="el-icon-menu" title="全部"></i>
        <span slot="title">全部</span>
      </el-menu-item>
      <el-menu-item index="1" title="图片">
        <i class="el-icon-picture"></i>
        <span slot="title">图片</span>
      </el-menu-item>
      <el-menu-item index="2" title="文档">
        <i class="el-icon-document"></i>
        <span slot="title">文档</span>
      </el-menu-item>
      <el-menu-item index="3" title="视频">
        <i class="el-icon-video-camera-solid"></i>
        <span slot="title">视频</span>
      </el-menu-item>
      <el-menu-item index="4" title="音乐">
        <i class="el-icon-headset"></i>
        <span slot="title">音乐</span>
      </el-menu-item>
      <el-menu-item index="5" title="其他">
        <i class="el-icon-takeaway-box"></i>
        <span slot="title">其他</span>
      </el-menu-item>
      <el-menu-item index="6" title="回收站">
        <i class="el-icon-box"></i>
        <span slot="title">回收站</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'AsideMenu',
  data() {
    return {
      fileListByFiletype: []
    }
  },
  computed: {
    //  当前活跃菜单项index，也是当前被选中的文件类型
    activeIndex: {
      get() {
        return String(this.$route.query.fileType)
      },
      set() {
        return '0'
      }
    },
  },
  created() {
  },
  methods: {
    //  导航菜单项点击事件
    handleSelect(index) {
      this.$router.push({
        path: '/file',
        query: { filePath: '/', fileType: index }
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
@import '~@/assets/styles/varibles.styl'
@import '~@/assets/styles/mixins.styl'

.aside-menu-wrapper
  position relative
  width 76px
  height 100%
  transition width 0.5s
  -webkit-transition width 0.5s
  .aside-title
    position absolute
    top 50%
    left 65px
    z-index 2
    width 12px
    height 100px
    line-height 100px
    cursor pointer
    z-index 3
    // transition left 0.5s
    // -webkit-transition left 0.5s
    &:hover
      opacity 0.7
    i {
      font-size 12px
    }
    .top
      position absolute
      top calc(50% - 50px)
      right 0px
      width: 0;
      height: 0;
      border-bottom: 12px solid transparent;
      border-right: 12px solid #fff;
    .bottom
      position absolute
      top calc(50% + 38px)
      right 0px
      width: 0;
      height: 0;
      border-top: 12px solid transparent;
      border-right: 12px solid #fff;
  >>> .el-menu
    border none
    border-right solid 1px $BorderLight
    width 64px
    height: calc(100vh - 60px);
    overflow: auto;
.expand
  width 232px
  .aside-title
    left 221px
  >>> .el-menu
    width 220px
    height: calc(100% - 60px);
    .el-menu-item
      padding-left 40px !important
</style>
