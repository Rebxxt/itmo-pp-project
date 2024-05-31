import {createStore} from "vuex";

export default function initStore() {
    return createStore({
        state() {
            return {
                selectedDay: null,
                selectedNote: null,
                noteChanges: null,
                calendar: null,
                notes: null,
                user: null,
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
            setUser(state, user) {
                state.user = user;
            },
        }
    })
}
