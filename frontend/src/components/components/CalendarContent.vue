<template>
  <div class="calendar-content">
    <div class="calendar-body" v-if="calendar">
      <CalendarDay
          v-for="(day, index) in calendar"
          :key="index"
          :day="day"
          @click="onSelectDay(day)"
      ></CalendarDay>
    </div>
    <div class="buttons">
      <button @click="onSelectDay(null)">Сбросить выбранное</button>
    </div>
  </div>
</template>

<script>
import CalendarDay from "@/components/components/CalendarDay.vue";
import {dayTypes, monthTypes} from "@/components/js/types";

export default {
  name: 'CalendarContent',
  components: {CalendarDay},
  data() {
    return {
      currentDate: new Date(),
      seconds: 0,
      loaded: false,
      calendar: [],
      selectedDay: null,
    }
  },
  methods: {
    onSelectDay(day) {
      if (day === null) {
        this.selectedDay = null;
      } else if (day === this.selectedDay) {
        this.selectedDay = null;
      } else if (day.monthType === monthTypes.MONTH_TYPE_CURRENT) {
        this.selectedDay = day;
      } else {
        this.selectedDay = null;
        console.log('wrong month')
      }
      this.$emit('selectDay', this.selectedDay)
    },
    initDays(date, from, to) {
      const calendar = [];

      let dateIndex = from
      while (dateIndex < to) {
        const curDate = new Date(dateIndex.getFullYear(), dateIndex.getMonth(), dateIndex.getDate());
        const monthType = dateIndex.getMonth() === date.getMonth()
            ? monthTypes.MONTH_TYPE_CURRENT
            : dateIndex.getMonth() > date.getMonth()
                ? monthTypes.MONTH_TYPE_NEXT
                : monthTypes.MONTH_TYPE_PREVIOUS
        const dayType = dateIndex.getDate() === date.getDate() && monthType === monthTypes.MONTH_TYPE_CURRENT
            ? dayTypes.DAY_TYPE_CURRENT
            : dateIndex.getDate() > date.getDate() && monthType !== monthTypes.MONTH_TYPE_PREVIOUS
            || monthType === monthTypes.MONTH_TYPE_NEXT
                ? dayTypes.DAY_TYPE_FUTURE
                : dayTypes.DAY_TYPE_PASSED;
        calendar.push({
          date: curDate,
          monthType: monthType,
          dayType: dayType,
        })
        dateIndex.setDate(dateIndex.getDate() + 1)
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
  },
  computed: {
    selectedNote() {
      return this.$store.state.selectedNote
    }
  },
  mounted() {
    this.getDateFrom(this.currentDate)
    const from = this.getDateFrom(this.currentDate)
    const to = this.getDateTo(this.currentDate)
    this.calendar = this.initDays(this.currentDate, from, to);

    this.loaded = true;
  }
}
</script>

<style scoped>
.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  width: fit-content;
  height: fit-content;
}
.calendar-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.buttons {
  display: flex;
  flex-direction: column;
  align-items: end;
}
</style>

