const axios = require('axios')
export class NoteService {
    constructor(host) {
        this.api = axios.create({
            baseURL: host,
            timeout: 1000,
            headers: {'X-Custom-Header': 'foobar'}
        });
    }

    post(text, date, userId) {
        return this.api.post(`note`, {}, { params: { user_id: userId, text, date }})
    }

    get(userId) {
        return this.api.get(`http://localhost:8080/note`, { params: { user_id: userId }})
    }

    delete(noteId) {
        return this.api.delete(`note`, { params: { note_id: noteId }})
    }
}
