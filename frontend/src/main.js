import AuthPage from "@/components/pages/AuthPage.vue";
import CalendarPage from "@/components/pages/CalendarPage.vue";
import NotFoundPage from "@/components/pages/NotFoundPage.vue";
import ProfilePage from "@/components/pages/ProfilePage.vue";
import {AuthService} from "@/services/http/auth.api";
import {NoteService} from "@/services/http/note.api";
import initStore from "@/store/main.store";
import {createApp} from 'vue'
import {createWebHistory, createRouter} from "vue-router";
import App from './App.vue'

const backendHost = "http://localhost:9090/"
const routes = [
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFoundPage },
    { path: '/', redirect: '/calendar' },
    { path: '/calendar', component: CalendarPage },
    { path: '/auth', component: AuthPage },
    { path: '/profile', component: ProfilePage },
]
const router = createRouter({
    history: createWebHistory(),
    routes
})


const app = createApp(App)
app.use(initStore())
app.use(router)
app.provide('$authService', new AuthService(backendHost))
app.provide('$noteService', new NoteService(backendHost))
app.mount('#app')
