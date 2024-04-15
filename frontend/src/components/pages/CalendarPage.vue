<template>
  <div class="calendar">
    <div class="calendar-header">
      <h4>Current Date: {{currentDate.getDate()}}</h4>
      <h4>Current Month: {{getMonthName(currentDate.getMonth())}}</h4>
      <h4>Current Year: {{currentDate.getFullYear()}}</h4>
    </div>
    <div class="flex">
      <div class="calendar-body" v-if="loaded">
        <CalendarDay
            v-for="(day, index) in calendar"
            :key="index"
            :day="day"
            @click="onSelectDay(day)"
        ></CalendarDay>
      </div>
      <NoteSidebar></NoteSidebar>
    </div>
  </div>
</template>

<script>
import CalendarDay from "@/components/components/CalendarDay.vue";
import NoteSidebar from "@/components/components/NoteSidebar.vue";
import { dayTypes, monthNames, monthTypes } from "@/components/js/types";

export default {
  name: 'CalendarPage',
  components: {NoteSidebar, CalendarDay},
  props: {
    msg: String,
  },
  methods: {
    onSelectDay(day) {
      if (day.monthType === monthTypes.MONTH_TYPE_CURRENT) {
        this.selectedDay = day;
      } else {
        console.log('wrong month')
      }
    },
    getDaysInMonth(year, month) {
      return new Date(year, month + 1, 0).getDate();
    },
    initDays(currentDate, from, to) {
      const calendar = [];

      let date = from
      while (date < to) {
        const curDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        const monthType = date.getMonth() === currentDate.getMonth()
            ? monthTypes.MONTH_TYPE_CURRENT
            : date.getMonth() > currentDate.getMonth()
                ? monthTypes.MONTH_TYPE_NEXT
                : monthTypes.MONTH_TYPE_PREVIOUS
        const dayType = date.getDate() === currentDate.getDate() && monthType === monthTypes.MONTH_TYPE_CURRENT
            ? dayTypes.DAY_TYPE_CURRENT
            : date.getDate() > currentDate.getDate() && monthType !== monthTypes.MONTH_TYPE_PREVIOUS
              || monthType === monthTypes.MONTH_TYPE_NEXT
                ? dayTypes.DAY_TYPE_FUTURE
                : dayTypes.DAY_TYPE_PASSED;
        calendar.push({
          currentDate: curDate,
          monthType: monthType,
          dayType: dayType,
        })
        date.setDate(date.getDate() + 1)
      }

      return calendar
    },
    getDateFrom(date) {
      const startDate = new Date(date.getFullYear(), date.getMonth(), 1)
      const daysToMonday = (startDate.getDay() + 6) % 7 || 7;

      return new Date(date.getFullYear(), date.getMonth(), startDate.getDate() - daysToMonday)
    },
    getDateTo(date) {
      const startDate = new Date(date.getFullYear(), date.getMonth() + 1, 1)
      const daysToNextMonday = 7 - (startDate.getDay() + 6) % 7;

      return new Date(date.getFullYear(), date.getMonth() + 1, startDate.getDate() + daysToNextMonday)
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

  },
  data() {
    return {
      currentDate: new Date(),
      seconds: 0,
      loaded: false,
      calendar: [],
      selectedDay: null,
    }
  },
  mounted() {
    this.getDateFrom(this.currentDate)
    const from = this.getDateFrom(this.currentDate)
    const to = this.getDateTo(this.currentDate)
    this.calendar = this.initDays(this.currentDate, from, to);

    this.$store.commit('increment')
    console.log(this.$store.state.count)

    this.loaded = true;
  }

}
</script>

<style scoped>
.calendar {
  height: 100vh;
}
.calendar-header {
  padding-top: 80px;
}
.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  width: fit-content;
  height: fit-content;
}
.flex {
  display: flex;
  gap: 16px;
}
</style>
