const axios = require('axios')

export class AuthApiService {
    constructor(host, authService) {
        this.api = axios.create({
            baseURL: host,
            timeout: 1000,
            headers: {}
        });
        this.authService = authService;
    }

    auth(login, password) {
        return this.api.post(`auth`, {}, { params: { user_login: login, password: password }})
    }

    get() {
        return this.api.get(`user`, { params: { user_login: this.authService.getUser() }})
    }

    create(login, password) {
        return this.api.post(`user`, {}, { params: { user_login: login, password: password }})
    }

    delete() {
        return this.api.delete(`user`, { params: { user_login: this.authService.getUser() }})
    }
}
