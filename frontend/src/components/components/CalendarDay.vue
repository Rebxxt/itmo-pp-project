<template>
  <div
      class="day"
      v-if="day"
      :class="[day.monthType, day.dayType, {
        'day-selected': isSelected,
      }]"
      @drop.prevent="onDrop"
      @dragenter.prevent
      @dragover.prevent
  >
    <div v-if="day.hasNotes" class="note-marker"></div>
    <h3>{{ day.date.getDate() }}</h3>
    <p>{{ getDayName }}</p>
  </div>
</template>

<script>
export default {
  name: 'CalendarDay',
  props: {
    id: Number,
    day: {
      date: Date,
      monthType: String,
      dayType: String,
      hasNotes: Boolean,
    },
    type: String,
  },
  computed: {
    getDayName() {
      return this.day.date.toLocaleString('ru', {weekday: 'long'})
    },
    isSelected() {
      const selected = this.$parent.selectedDay;
      return selected &&
          (selected === this.day ||
              (selected.date.getFullYear() === this.day.date.getFullYear() &&
                  selected.date.getMonth() === this.day.date.getMonth() &&
                  selected.date.getDate() === this.day.date.getDate()));
    },
  },
  methods: {
    onDrop() {
      const noteResult = this.$store.state.selectedNote;
      noteResult.createdAt.setDate(this.day.date.getDate())
      noteResult.createdAt.setMonth(this.day.date.getMonth())
      this.$store.commit('onChangeNote', noteResult)
    }
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

.day.day-selected {
  background: #ffffff !important;
  position: relative;
}

.note-marker {
  height: 5px;
  width: 5px;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
  border-radius: 50%;
  background: #527aff;
  position: absolute;
}
@keyframes onLoad {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>
