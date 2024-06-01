<template>
  <div class="container">
    <button @click="deleteUser">Удалить профиль</button>
  </div>
</template>

<script>
export default {
  name: "ProfilePage",
  inject: ['$authService', '$authApiService'],
  methods: {
    deleteUser() {
      this.$authApiService.delete().then(() => {
        this.$router.push('/authorization')
        this.$authService.clearData()
      })
    }
  }
}
</script>

<style scoped>

</style>
