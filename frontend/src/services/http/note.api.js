const axios = require('axios')

export class NoteApiService {
    constructor(host) {
        this.api = axios.create({
            baseURL: host,
            timeout: 1000,
            "Access-Control-Allow-Origin": host,
            "Access-Control-Allow-Methods": "POST,GET,PUT,DELETE,OPTIONS",
        });
    }

    post(note, date) {
        return this.api.post(`note`, { note, date })
    }

    put(note, date, noteId) {
        return this.api.put(`note`, { id: noteId, note, date })
    }

    get() {
        return this.api.get(`all_notes`)
    }

    delete(noteId) {
        return this.api.delete(`note`, { params: { id: noteId }})
    }
}
