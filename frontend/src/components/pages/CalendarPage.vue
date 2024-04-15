<template>
  <div class="calendar">
    <div class="calendar-header">
      <h4 class="header-date">{{currentDate.toLocaleString('ru', options)}}</h4>
      <h4 class="header-date">{{selectedDay ? selectedDay.date.toLocaleString('ru', options) : ''}}</h4>
    </div>
    <div class="flex">
      <CalendarContent @selectDay="onSelectDay"></CalendarContent>
      <NoteSidebar :selectedDay="selectedDay"></NoteSidebar>
    </div>
  </div>
</template>

<script>
import CalendarContent from "@/components/components/CalendarContent.vue";
import NoteSidebar from "@/components/components/NoteSidebar.vue";
import {monthNames} from "@/components/js/types";

export default {
  name: 'CalendarPage',
  components: {CalendarContent, NoteSidebar},
  methods: {
    getDaysInMonth(year, month) {
      return new Date(year, month + 1, 0).getDate();
    },
    getMonthName(month) {
      return [
        monthNames.MONTH_JANUARY,
        monthNames.MONTH_FEBRUARY,
        monthNames.MONTH_MARCH,
        monthNames.MONTH_APRIL,
        monthNames.MONTH_MAY,
        monthNames.MONTH_JUNE,
        monthNames.MONTH_JULY,
        monthNames.MONTH_AUGUST,
        monthNames.MONTH_SEPTEMBER,
        monthNames.MONTH_OCTOBER,
        monthNames.MONTH_NOVEMBER,
        monthNames.MONTH_DECEMBER,
      ][month];
    },
    onSelectDay(day) {
      console.log('on select...', day)
      this.selectedDay = day;
    }
  },
  data() {
    return {
      currentDate: new Date(),
      selectedDay: null,
      options: {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      },
    }
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
