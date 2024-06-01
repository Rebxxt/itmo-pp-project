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

      <div class="selected-day" v-if="selectedDay">
        <h4 class="header-date">{{ selectedDay.date.toLocaleString('ru', dateOptions) }}</h4>
      </div>

      <h4 class="header-date">{{ currentDate.toLocaleString('ru', dateFullOptions) }}</h4>
    </header>

    <section class="content">
      <RouterView />
    </section>
  </div>
</template>

<script>
import {DATE_OPTIONS, DATE_FULL_OPTIONS} from "@/components/js/types";

export default {
  name: 'CalendarModule',
  inject: ['$authService', '$authApiService'],
  data() {
    return {
      currentDate: new Date(),
      dateOptions: DATE_OPTIONS,
      dateFullOptions: DATE_FULL_OPTIONS
    }
  },
  methods: {
    logout() {
      this.$authService.clearData();
      this.$router.push('/auth');
    }
  },
  computed: {
    hasAuthorization() {
      return !!this.$authService.getUser() || this.$store.state.user
    },
    selectedDay() {
      return this.$store.state.selectedDay
    },
  },
  mounted() {
    if (this.$authService.getUser()) {
      this.$authApiService.get().then((response) => {
        if (!response.data) {
          this.$authService.clearData();
          this.$router.push('/authorization')
        }
      })
    }
  }
}
</script>

<style scoped>
nav {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin: 16px 0;
}

.content {
  margin: 0 16px;
}

.header-date:first-letter {
  text-transform: capitalize;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #333;
  color: white;
  margin-bottom: 32px;
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

.selected-day {
  margin: 0;
  font-size: 26px;
}
.selected-day h4 {
  margin: 8px 0;
}

</style>
