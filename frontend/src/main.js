import AuthPage from "@/components/pages/AuthPage.vue";
import CalendarPage from "@/components/pages/CalendarPage.vue";
import NotFoundPage from "@/components/pages/NotFoundPage.vue";
import ProfilePage from "@/components/pages/ProfilePage.vue";
import {AuthService} from "@/services/authorization/auth.service";
import {AuthApiService} from "@/services/http/auth.api";
import {NoteApiService} from "@/services/http/note.api";
import initStore from "@/store/main.store";
import axios from "axios";
import {createApp} from 'vue'
import VueAxios from "vue-axios";
import {createWebHistory, createRouter} from "vue-router";
import App from './App.vue'

const store = initStore();

const backendHost = "http://localhost:9090/"
const authService = new AuthService(store)
const authApiService = new AuthApiService(backendHost, authService)
const noteApiService = new NoteApiService(backendHost, authService)

const routes = [
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFoundPage },
    { path: '/', redirect: '/calendar' },
    { path: '/calendar', name: 'calendar', component: CalendarPage },
    { path: '/auth', name: 'authorization', component: AuthPage },
    { path: '/profile', name: 'profile', component: ProfilePage },
]
const router = createRouter({
    history: createWebHistory(),
    routes
})
router.beforeEach((to, from, next) => {
    const user = authService.getUser()
    if (to.name !== 'authorization') {
        if (user) {
            next()
        } else {
            next({ name: 'authorization' })
        }
    } else {
        if (user) {
            next({ name: 'calendar' })
        } else {
            next()
        }
    }
})


const app = createApp(App)
app.use(store)
app.use(router)
app.use(VueAxios, axios)
app.provide('$authService', authService)
app.provide('$authApiService', authApiService)
app.provide('$noteApiService', noteApiService)
app.mount('#app')
