import {createApp} from 'vue'
import {createStore} from "vuex";
import App from './App.vue'

const store = createStore({
    state() {
        return {
            selectedDay: null,
            selectedNote: null,
            noteChanges: null,
            calendar: null,
            notes: null,
        }
    },
    mutations: {
        onSelectDay(state, day) {
            state.selectedDay = day;
        },
        onSelectedNote(state, note) {
            state.selectedNote = note;
        },
        onChangeNote(state, note) {
            state.noteChanges = note;
        },
        setCalendar(state, calendar) {
            state.selectedDay = null;
            state.calendar = calendar;
        },
        setNotes(state, notes) {
            state.notes = notes;
        },
    }
})

const app = createApp(App)
app.use(store)
app.mount('#app')
