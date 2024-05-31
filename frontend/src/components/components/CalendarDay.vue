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
    <p v-if="anyLoading"><PulseLoader :loading="anyLoading"></PulseLoader></p>
    <p v-else>{{ getDayName }}</p>
  </div>
</template>

<script>
import PulseLoader from "vue-spinner/src/PulseLoader.vue";

export default {
  name: 'CalendarDay',
  components: {PulseLoader},
  inject: ['$noteApiService'],
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
  data() {
    return {
      loadings: 0,
    }
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
    anyLoading() {
      return this.loadings > 0
    },
  },
  methods: {
    onDrop() {
      this.loadings += 1;
      const temp = this.$store.state.selectedNote;
      temp.loading = true;
      const noteResult = {...this.$store.state.selectedNote};
      const year = this.day.date.getFullYear();
      const month = this.day.date.getMonth();
      const date = this.day.date.getDate();
      noteResult.createdAt = new Date(year, month, date)
      this.$noteApiService.put(noteResult.text, noteResult.createdAt.valueOf(), noteResult.id).then((response) => {
        const result = response.data
        const note = {
          text: result.text,
          id: result.id,
          createdAt: new Date(result.date)
        }
        console.log('on change note')
        this.$store.commit('onChangeNote', note)
        this.loadings -= 1;
      })
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

.month-previous-even {
  background: #bbbbbb !important;
}
.month-previous-odd {
  background: #cecece !important;
}

.month-current {
}

.month-next-even {
  background: #ffde93 !important;
}
.month-next-odd {
  background: #ffe9bb !important;
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
