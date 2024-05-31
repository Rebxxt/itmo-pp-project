<template>
  <div>
    <p>
      <strong>Current route path:</strong> {{ $route.fullPath }}
    </p>

    <nav>
      <RouterLink to="/calendar">Календарь</RouterLink>
      <RouterLink to="/profile">Пользователь</RouterLink>
      <RouterLink to="/auth">Авторизация</RouterLink>
    </nav>

    <RouterView />
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
}
</script>

<style scoped>
nav {
  display: flex;
  justify-content: center;
  gap: 8px;
}
</style>
