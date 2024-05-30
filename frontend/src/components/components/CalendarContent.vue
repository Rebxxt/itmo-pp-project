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

    <h4>
      {{
        selectedMonth?.toLocaleString('ru', {
          year: "numeric",
          month: "long",
        })
      }}
    </h4>

    <div class="buttons">
      <div class="selector">
        <b>Режим отображения: </b>
        <button>Текущий месяц</button>
        <button>Текущая неделя</button>
        <button>Текущий день</button>
      </div>

      <div class="selector">
        <b>Смена просматриваемой даты: </b>
        <button @click="onChangeYear(-1)">Прошлый год</button>
        <button @click="onChangeMonth(-1)">Прошлый месяц</button>
        <button @click="onChangeMonth(0)">Сегодняшний день</button>
        <button @click="onChangeMonth(1)">Следующий месяц</button>
        <button @click="onChangeYear(1)">Следующий год</button>
      </div>


      <div class="selector">
        <button @click="onSelectDay(null)">Сбросить выбранное</button>
      </div>
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
    getDateFrom(date) {
      const startDate = new Date(date.getFullYear(), date.getMonth(), 1);
      const daysToMonday = (startDate.getDay() + 6) % 7 || 7;
      console.log('get date from', date, startDate, daysToMonday)
      return new Date(date.getFullYear(), date.getMonth(), startDate.getDate() - daysToMonday);
    },
    getDateTo(date) {
      const startDate = new Date(date.getFullYear(), date.getMonth() + 1, 1);
      const daysToNextMonday = 7 - (startDate.getDay() + 6) % 7;
      console.log('get date to', date, startDate, daysToNextMonday)

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
    }
  },
  computed: {
    selectedNote() {
      return this.$store.state.selectedNote;
    },
    notes() {
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

.selector {
  margin-bottom: 16px;
  display: flex;
  gap: 4px;
}
</style>

