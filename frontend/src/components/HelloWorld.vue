<template>
  <div>
    <header>
      <nav v-if="hasAuthorization">
        <ul class="nav-links">
          <li>
            <RouterLink to="/calendar">Календарь</RouterLink>
          </li>
          <li>
            <RouterLink to="/profile">Пользователь</RouterLink>
          </li>
          <li>
            <a @click="logout" class="link">Выход</a>
          </li>
        </ul>
      </nav>
    </header>

    <section class="content">
      <RouterView />
    </section>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  inject: ['$authService'],
  methods: {
    logout() {
      this.$authService.clearData();
      this.$router.push('/auth');
    }
  },
  computed: {
    hasAuthorization() {
      return !!this.$authService.getUser() || this.$store.state.user
    }
  },
}
</script>

<style scoped>
nav {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 40px;
}
.top {
  padding-top: 80px;
}

.content {
  margin: 0 16px;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #333;
  color: white;
}

.logo img {
  height: 50px;
}

nav .nav-links {
  list-style: none;
  display: flex;
}

nav .nav-links li {
  margin: 0 15px;
}

nav .nav-links a {
  color: white;
  text-decoration: none;
  font-weight: bold;
}

nav .nav-links a:hover {
  color: #1e73be;
}
</style>
