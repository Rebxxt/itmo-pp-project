<template>
  <div class="calendar">
    <div class="flex">
      <CalendarContent :delete-note="deleteNote.bind(this)"></CalendarContent>
      <NoteSidebar :loading="loading" :add-note="addNote.bind(this)"></NoteSidebar>
    </div>
  </div>
</template>

<script>
import CalendarContent from "@/components/components/CalendarContent.vue";
import NoteSidebar from "@/components/components/NoteSidebar.vue";
import {ref} from "vue";

export default {
  name: 'CalendarPage',
  components: {CalendarContent, NoteSidebar},
  inject: ['$noteApiService'],
  data() {
    return {
      loading: true,
      notes: [],
    }
  },
  methods: {
    getNotes() {
      this.loading = true;
      this.$noteApiService.get().then((response) => {
        this.notes = response.data.map((v) => ({
          createdAt: new Date(v.date),
          text: v.note,
          id: v.id,
        }));
        this.setNotes(this.notes)
        this.loading = false;
      })
    },
    addNote(note) {
      this.$noteApiService.post(note.text, note.createdAt.valueOf()).then((response) => {
        if (!response.data) {
          console.error('No body', response)
          return
        }
        note.id = response.data.id
        if (this.selectedDay) {
          note.createdAt.setDate(this.selectedDay.date.getDate());
          note.createdAt.setMonth(this.selectedDay.date.getMonth());
          note.createdAt.setYear(this.selectedDay.date.getFullYear());
        }
        this.notes.push(note);
        this.setNotes(this.notes);
      })
    },
    deleteNote(noteId) {
      console.log('deelte', noteId)
      const index = this.notes.findIndex(v => v.id === noteId)
      console.log('INDEX', index, this.notes.map(v => v.id))
      this.notes.splice(index, 1)
      this.setNotes(this.notes)
    },
    setNotes(notes) {
      const result = [...notes].map(v => ({...v, loading: ref(false)}));
      this.$store.commit('setNotes', result)
    },
  },
  computed: {
    selectedDay() {
      return this.$store.state.selectedDay
    },
    noteChanges() {
      return this.$store.state.noteChanges
    },
  },
  created() {
    this.getNotes()
  },
  watch: {
    noteChanges(newNote) {
      if (newNote) {
        const note = this.notes.find(v => v.id === newNote.id);
        note.loading = false;
        Object.assign(note, newNote);
        this.$store.commit('onChangeNote', null);
        this.setNotes(this.notes)
      }
    }
  },
}
</script>

<style scoped>
.flex {
  display: flex;
  gap: 16px;
}
</style>
