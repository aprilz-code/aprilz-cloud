import Vue from "vue";
import Vuex from "vuex";

import user from './module/user'
import fileList from './module/fileList'
import sideMenu from './module/sideMenu'
import imgReview from './module/imgReview'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    //
  },
  getters: {
    isLogin: (state) => state.user.isLogin,
    username: (state) => state.user.username,
    userId: (state) => state.user.userId,
    userImgUrl: (state) => state.user.userImgUrl,
    userInfoObj: (state) => state.user.userInfoObj,
    selectedColumnList: (state) =>
      state.fileList.selectedColumnList === null
        ? ["extendName", "fileSize", "uploadTime", "deleteTime"]
        : state.fileList.selectedColumnList.split(","), //  列显隐
    //fileModel: (state) => state.fileList.fileModel === null ? 0 : Number(state.fileList.fileModel), //  文件展示模式，0列表模式，1网格模式 2 时间线模式
  },
  mutations: {
    //
  },
  actions: {
    //
  },
  modules: {
    user,
    fileList,
    sideMenu,
    imgReview
  }
})
