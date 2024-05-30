import {AuthService} from "@/services/http/auth.api";
import {NoteService} from "@/services/http/note.api";
import initStore from "@/store/main.store";
import {createApp} from 'vue'
import App from './App.vue'

const backendHost = "localhost:8080/"

const app = createApp(App)
app.use(initStore())
app.provide('$authService', new AuthService(backendHost))
app.provide('$noteService', new NoteService(backendHost))
app.mount('#app')
