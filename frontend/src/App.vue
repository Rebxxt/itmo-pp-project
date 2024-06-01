<template>
  <div class="container">
    <CalendarModule />
  </div>
</template>

<script>
import CalendarModule from './components/CalendarModule.vue'

export default {
  name: 'App',
  components: {
    CalendarModule
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin: 0;
}
body {
  margin: 0 !important;
  padding: 0 !important;
}

.link {
  cursor: pointer;
}
</style>
