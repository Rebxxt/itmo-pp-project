const axios = require('axios')

export class AuthService {
    constructor(host) {
        this.api = axios.create({
            baseURL: host,
            timeout: 1000,
            headers: {}
        });
    }

    auth(login, password) {
        return this.api.post(`auth`, {}, { params: { user_name: login, password: password }})
    }

    get(userId) {
        return this.api.get(`user`, { params: { user_id: userId }})
    }

    create(login, password) {
        return this.api.post(`user`, {}, { params: { user_name: login, password: password }})
    }
}
