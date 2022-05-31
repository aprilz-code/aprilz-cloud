export default {
  state: {
    selectedColumnList: sessionStorage.getItem("selectedColumnList"), //  列显隐
  },
  mutations: {
    changeSelectedColumnList(state, data) {
      sessionStorage.setItem("selectedColumnList", data.toString());
      state.selectedColumnList = data.toString();
    }

  },
  actions: {
  }
}
