import {initNotes} from "@/services/http/mock";

const axios = require('axios')
export class NoteService {
    constructor(host) {
        this.api = axios.create({
            baseURL: host,
            timeout: 1000,
            "Access-Control-Allow-Origin": host,
            "Access-Control-Allow-Methods": "POST,GET,PUT,DELETE,OPTIONS",
        });
    }

    post(text, date, userId) {
        return this.api.post(`note`, {}, { params: { user_id: userId, text, date }})
    }

    put(text, date, noteId) {
        return this.api.put(`note`, {}, { params: { note_id: noteId, text, date }})
    }

    get(userId) {
        return this.api.get(`note`, { params: { user_login: userId }})
    }

    delete(noteId) {
        return this.api.delete(`note`, { params: { note_id: noteId }})
    }
}
export class NoteMockService {
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

    put(text, date, noteId) {
        // console.log('put for noteId', noteId)
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({
                    id: noteId,
                    createdAt: date,
                    text,
                })
            }, 1000)
        })
    }

    get(userId) {
        console.log('get for userid', userId)
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(initNotes)
            }, 1000)
        })
    }

    delete(noteId) {
        return this.api.delete(`note`, { params: { note_id: noteId }})
    }
}
