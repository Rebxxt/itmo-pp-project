<template>
  <div>
    <CalendarPage></CalendarPage>
  </div>
</template>

<script>
import CalendarPage from "@/components/pages/CalendarPage.vue";

export default {
  name: 'HelloWorld',
  components: {CalendarPage},
  props: {
    msg: String
  }
}
</script>

<style scoped>

</style>
