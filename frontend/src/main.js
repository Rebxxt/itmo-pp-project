import {createApp} from 'vue'
import {createStore} from "vuex";
import App from './App.vue'

const store = createStore({
    state() {
        return {
            selectedNote: null,
            noteChanges: null,
        }
    },
    mutations: {
        onSelectedNote(state, note) {
            state.selectedNote = note;
        },
        onChangeNote(state, note) {
            state.noteChanges = note;
        },
    }
})

const app = createApp(App)
app.use(store)
app.mount('#app')
