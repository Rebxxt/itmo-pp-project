const axios = require('axios')

export class NoteApiService {
    constructor(host, authService) {
        this.api = axios.create({
            baseURL: host,
            timeout: 1000,
            "Access-Control-Allow-Origin": host,
            "Access-Control-Allow-Methods": "POST,GET,PUT,DELETE,OPTIONS",
        });

        this.authService = authService;
    }

    post(text, date) {
        return this.api.post(`note`, {}, { params: { user_login: this.authService.getUser(), text, date }})
    }

    put(text, date, noteId) {
        return this.api.put(`note`, {}, { params: { note_id: noteId, text, date, user_login: this.authService.getUser() }})
    }

    get() {
        return this.api.get(`note`, { params: { user_login: this.authService.getUser() }})
    }

    delete(noteId) {
        return this.api.delete(`note`, { params: { note_id: noteId }})
    }
}
