<template>
  <div class="calendar-content">
    <h4 class="date">
      {{
        selectedMonth?.toLocaleString('ru', {
          year: "numeric",
          month: "long",
        })
      }}
    </h4>

    <div class="calendar-body" v-if="calendar">
      <CalendarDay
          v-for="(day, index) in calendar"
          :key="index"
          :day="day"
          @click="onSelectDay(day)"
      ></CalendarDay>
    </div>

    <div class="selector buttons-changer">
      <button class="back-orange" @click="onChangeYear(-1)">—год</button>
      <button class="back-yellow" @click="onChangeMonth(-1)">—месяц</button>
      <button class="back-green" @click="onChangeMonth(0)">Текущий месяц</button>
      <button class="back-yellow" @click="onChangeMonth(1)">+месяц</button>
      <button class="back-orange" @click="onChangeYear(1)">+год</button>
    </div>

    <div class="footer">
      <Transition>
        <div
            class="drop-delete"
            v-if="selectedNote"
            @drop.prevent="onDrop"
            @dragenter.prevent
            @dragover.prevent
        >
          <b>Удалить</b>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script>
import CalendarDay from "@/components/components/CalendarDay.vue";
import {dayTypes, monthTypes} from "@/components/js/types";
import {dateDiff, isSameDate} from "@/components/js/utils";

export default {
  name: 'CalendarContent',
  components: {CalendarDay},
  inject: ['$noteApiService'],
  props: {
    deleteNote: Function,
  },
  data() {
    return {
      selectedMonth: new Date(),
      currentDate: new Date(),
      seconds: 0,
      calendar: [],
      selectedDay: null,
    }
  },
  methods: {
    onSelectDay(day) {
      if (day && day.date.getMonth() !== this.selectedMonth.getMonth()) {
        let step = 0;
        if (
            day.date.getMonth() > this.selectedMonth.getMonth() &&
            day.date.getFullYear() === this.selectedMonth.getFullYear() ||
            day.date.getFullYear() > this.selectedMonth.getFullYear()
        ) {
          step = 1;
        } else {
          step = -1;
        }
        this.onChangeMonth(step)
        this.selectedDay = day;
        this.$store.commit('onSelectDay', this.selectedDay)
        return
      }
      if (!day) {
        this.selectedDay = null;
      } else if (day === this.selectedDay || this.selectedDay && day && isSameDate(day.date, this.selectedDay.date)) {
        this.selectedDay = null;
      } else {
        this.selectedDay = day;
      }
      this.$store.commit('onSelectDay', this.selectedDay)
    },
    initDays(today, from, to) {
      const calendar = [];

      let dateIndex = from
      if (dateDiff(from, to) <= 7 * 5) {
        to.setDate(to.getDate() + 7)
      }
      while (dateIndex < to) {
        const curDate = new Date(dateIndex.getFullYear(), dateIndex.getMonth(), dateIndex.getDate());
        const monthType = dateIndex.getMonth() === today.getMonth() && dateIndex.getFullYear() === today.getFullYear()
            ? monthTypes.MONTH_TYPE_CURRENT
            : dateIndex.getMonth() > today.getMonth() && dateIndex.getFullYear() === today.getFullYear() || dateIndex.getFullYear() > today.getFullYear()
                ? (dateIndex.getMonth() % 2 === 0 ? monthTypes.MONTH_TYPE_NEXT_EVEN : monthTypes.MONTH_TYPE_NEXT_ODD)
                : (dateIndex.getMonth() % 2 === 0 ? monthTypes.MONTH_TYPE_PREVIOUS_EVEN : monthTypes.MONTH_TYPE_PREVIOUS_ODD)
        const dayType = dateIndex.getDate() === today.getDate() && monthType === monthTypes.MONTH_TYPE_CURRENT
            ? dayTypes.DAY_TYPE_CURRENT
            : dateIndex.getDate() > today.getDate() && monthType !== monthTypes.MONTH_TYPE_PREVIOUS
            || (monthType === monthTypes.MONTH_TYPE_NEXT_EVEN || monthType === monthTypes.MONTH_TYPE_NEXT_ODD)
                ? dayTypes.DAY_TYPE_FUTURE
                : dayTypes.DAY_TYPE_PASSED;
        calendar.push({
          date: curDate,
          monthType: monthType,
          dayType: dayType,
          hasNotes: false,
        })
        dateIndex.setDate(dateIndex.getDate() + 1);
      }

      this.selectedDay = null;
      this.$store.commit('onSelectDay', this.selectedDay);

      return calendar
    },
    onDrop() {
      console.log('on drop', this.$store.state.selectedNote.id, this.$store.state.selectedNote.text )
      const noteToDelete = this.$store.state.selectedNote.id;
      this.$noteApiService.delete(noteToDelete).then(() => {
        this.deleteNote(noteToDelete)
      })
    },
    getDateFrom(date) {
      const startDate = new Date(date.getFullYear(), date.getMonth(), 1);
      const daysToMonday = (startDate.getDay() + 6) % 7 || 7;
      return new Date(date.getFullYear(), date.getMonth(), startDate.getDate() - daysToMonday);
    },
    getDateTo(date) {
      const startDate = new Date(date.getFullYear(), date.getMonth() + 1, 1);
      const daysToNextMonday = 7 - (startDate.getDay() + 6) % 7;

      return new Date(date.getFullYear(), date.getMonth() + 1, startDate.getDate() + daysToNextMonday);
    },
    onChangeMonth(value) {
      if (value === 0) {
        this.selectedMonth = new Date(this.currentDate);
      } else {
        this.selectedMonth = new Date(this.selectedMonth.getFullYear(), this.selectedMonth.getMonth() + value)
      }
      const from = this.getDateFrom(this.selectedMonth);
      const to = this.getDateTo(this.selectedMonth);
      this.setCalendar(this.initDays(this.currentDate, from, to));
    },
    onChangeYear(value) {
      this.selectedMonth.setFullYear(this.selectedMonth.getFullYear() + value);
      const from = this.getDateFrom(this.selectedMonth);
      const to = this.getDateTo(this.selectedMonth);
      this.setCalendar(this.initDays(this.currentDate, from, to));
    },
    setCalendar(calendar) {
      this.calendar = calendar;
      this.$store.commit('setCalendar', this.calendar);
      this.updateNotesInfo()
    },
    updateNotesInfo() {
      const notes = this.$store.state.notes
      if (!notes?.length) return
      const newNotes = notes.map(v => v.createdAt)
      for (const day of this.calendar) {
        const temp = newNotes.find(note => {
          return isSameDate(note, day.date)
        })
        day.hasNotes = !!temp;
      }
    },
    findByDate(date) {
      return this.calendar.find(v => {
        if (isSameDate(v.date, date)) return v
      })
    }
  },
  computed: {
    selectedNote() {
      return this.$store.state.selectedNote;
    },
    notes() {
      this.updateNotesInfo()
      return this.$store.state.notes
    }
  },
  watch: {
    notes() {
      this.updateNotesInfo()
    }
  },
  mounted() {
    const from = this.getDateFrom(this.selectedMonth);
    const to = this.getDateTo(this.selectedMonth);
    this.setCalendar(this.initDays(this.selectedMonth, from, to));
  }
}
</script>

<style scoped>
.drop-delete {
  height: 100px;
  width: 150px;
  border: 4px solid #fd6363;
  border-radius: 8px;
  background: #ffc8c8;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

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
  margin-top: 32px;
}
.buttons button {
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 16px;
}
.buttons-changer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0 !important;
}
.buttons-changer button {
  flex: 1;
  border: none;
  padding: 8px 16px;
  font-size: 16px;
  transition: 0.5s;
  cursor: pointer;
}
.buttons-changer button:hover {

}
.buttons-changer button:first-child {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}
.buttons-changer button:last-child {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}

.selector {
  margin-bottom: 16px;
  display: flex;
  gap: 4px;
}

.date {
  font-size: 22px;
  margin: 0;
}
.date:first-letter {
  text-transform: capitalize;
}

.back-green {
  background: #aff169;
}
.back-green:hover {
  background: #d3ffa3;
}

.back-yellow {
  background: #ffe226;
}
.back-yellow:hover {
  background: #ffee8e;
}

.back-orange {
  background: #ffc226;
}
.back-orange:hover {
  background: #ffda78;
}

.footer {
  display: flex;
  justify-content: end;
  align-items: center;
}
.v-enter-active,
.v-leave-active {
  transition: opacity 0.2s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>

