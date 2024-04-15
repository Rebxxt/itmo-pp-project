<template>
  <div
      class="day"
      v-if="day"
      :class="[day.monthType, day.dayType, {
        'day-selected': isSelected,
      }]"
  >
    <h3>{{ day.currentDate.getDate() }}</h3>
    <p>{{getDayName}}</p>
  </div>
</template>

<script>
export default {
  name: 'CalendarDay',
  props: {
    id: Number,
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
    },
    isSelected() {
      return this.$parent.selectedDay === this.day;
    }
  },
  methods: {
  },
}
</script>

<style scoped>
.day {
  background: #c3ddff;
  border-radius: 8px;
  width: 100px;
  height: 100px;
  padding: 4px;

  box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;

  -webkit-user-select: none;
  -ms-user-select: none;
  user-select: none;
  transition: .2s;
}
.day:hover {
  cursor: pointer;
  box-shadow: rgba(0, 0, 0, 0.24) 0px 5px 12px;
}
.month-previous {
  background: #cecece !important;
}
.month-current {
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
  background: #ffc226 !important;
}
.day-future {
}
.month-current.day-selected {
  background: #ffffff !important;
}
</style>
