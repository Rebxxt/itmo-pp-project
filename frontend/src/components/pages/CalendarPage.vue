<template>
  <div class="calendar">
    <h4>Current Date: {{currentDate.getDate()}}</h4>
    <h4>Current Month: {{getMonthName(currentDate.getMonth())}}</h4>
    <div class="calendar-body" v-if="loaded">
      <CalendarDay
          v-for="day in calendar"
          :key="day"
          :day="day"
      ></CalendarDay>
    </div>
  </div>
</template>

<script>
import CalendarDay from "@/components/components/CalendarDay.vue";

export const MONTH_JANUARY = "Январь";
export const MONTH_FEBRUARY = "Февраль";
export const MONTH_MARCH = "Март";
export const MONTH_APRIL = "Апрель";
export const MONTH_MAY = "Май";
export const MONTH_JUNE = "Июнь";
export const MONTH_JULY = "Июль";
export const MONTH_AUGUST = "Август";
export const MONTH_SEPTEMBER = "Сентябрь";
export const MONTH_OCTOBER = "Октябрь";
export const MONTH_NOVEMBER = "Ноябрь";
export const MONTH_DECEMBER = "Декабрь";

export const MONTH_TYPE_PREVIOUS = "month-previous"
export const MONTH_TYPE_CURRENT = "month-current"
export const MONTH_TYPE_NEXT = "month-next"

export const DAY_TYPE_PASSED = "day-passed"
export const DAY_TYPE_CURRENT = "day-current"
export const DAY_TYPE_FUTURE = "day-future"

export default {
  name: 'CalendarPage',
  components: {CalendarDay},
  props: {
    msg: String,
  },
  methods: {
    getDaysInMonth(year, month) {
      return new Date(year, month + 1, 0).getDate();
    },
    initDays(currentDate, from, to) {
      const calendar = [];
      const daysInMonth = this.getDaysInMonth(currentDate.getFullYear(), currentDate.getMonth());
      const prevMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 0)
      const lastDayOfPrevMonth = prevMonth.getDate();
      const nextMonth = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1)

      for (let day = from.getDate(); day <= lastDayOfPrevMonth; ++day) {
        calendar.push({
          currentDate: new Date(prevMonth.getFullYear(), prevMonth.getMonth(), day),
          monthType: MONTH_TYPE_PREVIOUS,
          dayType: DAY_TYPE_PASSED
        })
      }

      for (let day = 1; day <= daysInMonth; ++day) {
        const dayType = day === currentDate.getDate()
            ? DAY_TYPE_CURRENT
            : day > currentDate.getDate()
                ? DAY_TYPE_FUTURE
                : DAY_TYPE_PASSED
        calendar.push({
          currentDate: new Date(currentDate.getFullYear(), currentDate.getMonth(), day),
          monthType: MONTH_TYPE_CURRENT,
          dayType: dayType
        })
      }

      for (let day = nextMonth.getDate(); day < to.getDate(); ++day) {
        calendar.push({
          currentDate: new Date(nextMonth.getFullYear(), nextMonth.getMonth(), day),
          monthType: MONTH_TYPE_NEXT,
          dayType: DAY_TYPE_FUTURE
        })
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

      return new Date(date.getFullYear(), date.getMonth(), startDate.getDate() + daysToNextMonday)
    },
    getMonthName(month) {
      return [
        MONTH_JANUARY,
        MONTH_FEBRUARY,
        MONTH_MARCH,
        MONTH_APRIL,
        MONTH_MAY,
        MONTH_JUNE,
        MONTH_JULY,
        MONTH_AUGUST,
        MONTH_SEPTEMBER,
        MONTH_OCTOBER,
        MONTH_NOVEMBER,
        MONTH_DECEMBER,
      ][month];
    },

  },
  data() {
    return {
      currentDate: new Date(),
      seconds: 0,
      loaded: false,
      calendar: [],
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
.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
</style>
