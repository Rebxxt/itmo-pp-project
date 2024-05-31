<template>
  <div class="container authorization" v-if="authType === types.AUTHORIZATION_TYPE_SIGN_IN">
    <h3>Авторизация</h3>
    <form class="auth-form" @submit.prevent="onSubmitAuthorization">
      <input :disabled="loading" type="text" placeholder="Логин..." v-model="login">
      <input :disabled="loading" type="password" placeholder="Пароль..." v-model="password">
      <button :disabled="loading" type="submit">Войти</button>
    </form>
    <button @click="changeType(types.AUTHORIZATION_TYPE_SIGN_UP)">Нет аккаунта? Зарегистрироваться</button>
    <p v-if="validatorMessage">{{validatorMessage}}</p>
  </div>

  <div class="container registration" v-else-if="authType === types.AUTHORIZATION_TYPE_SIGN_UP">
    <h3>Регистрация</h3>
    <form class="auth-form" @submit.prevent="onSubmitRegistration()">
      <input :disabled="loading" type="text" placeholder="Логин..." v-model="login">
      <input :disabled="loading" type="password" placeholder="Пароль..." v-model="password">
      <input :disabled="loading" type="password" placeholder="Повторите пароль..." v-model="passwordRepeated">
      <button :disabled="loading" type="submit">Зарегистрироваться</button>
    </form>
    <button @click="changeType(types.AUTHORIZATION_TYPE_SIGN_IN)">Есть аккаунт? Авторизоваться</button>
    <p v-if="validatorMessage">{{validatorMessage}}</p>
  </div>

  <div v-else class="unknown-type">Выберите авторизацию/регистрацию</div>
</template>

<script>
import {authorizationTypes} from "@/components/js/types";

export default {
  name: "AuthPage",
  inject: ['$authApiService', "$authService"],
  data() {
    return {
      loading: false,
      login: '',
      password: '',
      passwordRepeated: '',
      validatorMessage: '',
      authType: authorizationTypes.AUTHORIZATION_TYPE_SIGN_IN,
      types: authorizationTypes,
    }
  },
  methods: {
    onSubmitAuthorization() {
      this.$authApiService.auth(this.login, this.password).then((response) => {
        this.$authService.clearData();
        if (response.data) {
          this.$authService.setUser(this.login)
          this.$router.push('/calendar')
        } else {
          this.invalidateMessage('Неверный логин/пароль')
        }
      })
    },
    onSubmitRegistration() {
      if (this.password !== this.passwordRepeated) {
        this.invalidateMessage('Пароли не совпадают')
        return
      }
      this.$authApiService.create(this.login, this.password).then((response) => {
        this.$authService.clearData();
        if (response.data) {
          this.$authService.setUser(this.login)
          this.$router.push('/calendar')
        } else {
          this.invalidateMessage('Неверный логин/пароль')
        }
      })
    },
    changeType(type) {
      this.authType = type;
      this.password = '';
      this.passwordRepeated = '';
    },
    invalidateMessage(message) {
      this.validatorMessage = message
      setTimeout(() => {
        this.validatorMessage = ''
      }, 5000)
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 16px;
}

.auth-form {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  gap: 16px;
}
</style>
