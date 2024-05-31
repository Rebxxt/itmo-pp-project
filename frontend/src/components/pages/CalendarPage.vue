<template>
  <div class="calendar">

    <div class="calendar-header">
      <h4 class="header-date">{{currentDate.toLocaleString('ru', options)}}</h4>
      <h4 class="header-date">{{selectedDay ? selectedDay.date.toLocaleString('ru', options) : ''}}</h4>
    </div>

    <div class="flex">
      <CalendarContent></CalendarContent>
      <NoteSidebar :selectedDay="selectedDay"></NoteSidebar>
    </div>
  </div>
</template>

<script>
import CalendarContent from "@/components/components/CalendarContent.vue";
import NoteSidebar from "@/components/components/NoteSidebar.vue";

export default {
  name: 'CalendarPage',
  components: {CalendarContent, NoteSidebar},
  methods: {
    onSelectDay(day) {
      this.selectedDay = day;
    }
  },
  data() {
    return {
      currentDate: new Date(),
      options: {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      },
    }
  },
  computed: {
    selectedDay() {
      return this.$store.state.selectedDay
    },
  },
  mounted() {

  }

}
</script>

<style scoped>
.header-date:first-letter {
  text-transform: capitalize;
}
.calendar {
  height: 100vh;
}
.calendar-header {
  padding-top: 80px;
  display: flex;
  justify-content: space-between;
}
.flex {
  display: flex;
  gap: 16px;
}
</style>
