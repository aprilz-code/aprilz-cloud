
export default {
  state: {
    storageValue: 0, //  已使用存储容量
    storageMaxValue: Math.pow(1024, 3) * 100 //  最大存储容量，100GB
  },
  mutations: {
    setStorageValue(state, data) {
      state.storageValue = data;
    }
  },
  actions: {
  }
}
