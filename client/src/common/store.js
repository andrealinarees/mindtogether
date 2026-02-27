import { ref } from "vue";

const store = ref({
  state: {
    user: {
      authority: "",
      login: "",
      name: "",
      logged: false
    }
  }
});

export { getStore };

function getStore() {
  return store.value;
}
