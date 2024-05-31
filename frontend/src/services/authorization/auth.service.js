export class AuthService {
    userField = 'user'
    constructor(store) {
        this.$store = store
    }

    clearData() {
        localStorage.clear()
    }

    setUser(userLogin) {
        this.$store.commit('setUser', userLogin)
        localStorage.setItem(this.userField, userLogin)
    }

    getUser() {
        const user = localStorage.getItem(this.userField)
        this.$store.commit('setUser', user)
        return this.$store.state.user;
    }
}
