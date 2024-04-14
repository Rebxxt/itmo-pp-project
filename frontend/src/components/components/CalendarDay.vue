<template>
  <div class="day" v-if="day" :class="[day.monthType, day.dayType]">
    <h3>{{ day.currentDate.getDate() }}</h3>
    {{getDayName}}
  </div>
</template>

<script>
export default {
  name: 'CalendarDay',
  props: {
    day: {
      currentDate: Date,
      monthType: String,
      dayType: String,
    },
    type: String,
  },
  computed: {
    getDayName() {
      return this.day.currentDate.toLocaleString('ru', {weekday: 'long'})
    }
  }
}
</script>

<style scoped>
.day {
  background: #c3ddff;
  border-radius: 8px;
  width: 100px;
  height: 100px;
  padding: 4px;
}
.month-previous {
  background: #cecece !important;
}
.month-current+.day-current {
  background: #ffc226 !important;
}
.month-next {
  background: #ffe8b4 !important;
}
.day-passed {
  color: #868686
}
.day-current {
  color: #ee0a0a;
  font-weight: bold;
}
.day-future {
}
</style>
