<template>
  <div class="container">
    <div class="body">
      <h4 style="margin-top: 0;">Заметки</h4>

      <NoteCreator @onCreateNote="addNote" :loading="loading"></NoteCreator>

      <div v-if="loading">
        <PulseLoader class="loader" :loading="loading"></PulseLoader>
      </div>

      <div class="list" v-else-if="filteredNotes.length > 0">
        <div
            :draggable="!note.loading"
            @dragstart="startDrag($event, note)"
            @dragend="endDrag"
            @click="onClickNote(note)"
            class="note"
            v-for="(note, index) in filteredNotes"
            :key="index"
        >
          <span>{{note.text}}</span>

          <PulseLoader v-if="note.loading" class="note-loader" :loading="note.loading"></PulseLoader>
          <span v-else>{{note.createdAt?.toLocaleDateString('ru')}}</span>

        </div>
      </div>

      <div class="list-empty" v-else>
        <h4>Список пуст</h4>
      </div>
    </div>
  </div>
</template>

<script>
import NoteCreator from "@/components/components/NoteCreator.vue";
import {ref} from "vue";
import PulseLoader from 'vue-spinner/src/PulseLoader.vue'

export default {
  name: 'NoteSidebar',
  components: {NoteCreator, PulseLoader},
  inject: ['$noteService'],
  props: {
    selectedDay: {
      date: Date,
    },
  },
  methods: {
    addNote(note) {
      if (this.selectedDay) {
        note.createdAt.setDate(this.selectedDay.date.getDate());
        note.createdAt.setMonth(this.selectedDay.date.getMonth());
        note.createdAt.setYear(this.selectedDay.date.getFullYear());
      }
      this.notes.push(note);
      this.setNotes(this.notes);
    },
    onClickNote(note) {
      console.log('clicked on note:', note)
    },
    startDrag(e, note) {
      this.selectedNoteToDrag = note;
      this.$store.commit('onSelectedNote', this.selectedNoteToDrag)
    },
    endDrag() {
      this.selectedNoteToDrag = null;
      this.$store.commit('onSelectedNote', this.selectedNoteToDrag)
    },
    setNotes(notes) {
      this.notes = [...notes].map(v => ({...v, loading: ref(false)}));
      this.$store.commit('setNotes', this.notes)
    },
    initNotes() {
      this.loading = true;
      this.$noteService.get('TODO_USER').then((response) => {
        this.setNotes(response)
        this.loading = false;
        console.log('response', response)
      })
    }
  },
  computed: {
    filteredNotes() {
      const reversed = [...this.notes]
      reversed.sort((a, b) => b.createdAt-a.createdAt)
      if (this.selectedDay) {
        return reversed.filter(v => (
            v.createdAt.getDate() === this.selectedDay.date.getDate() &&
            v.createdAt.getMonth() === this.selectedDay.date.getMonth()
        ))
      }
      return reversed
    },
    noteChanges() {
      return this.$store.state.noteChanges
    },
  },
  watch: {
    noteChanges(newNote) {
      if (newNote) {
        const note = this.notes.find(v => v.id === newNote.id);
        note.loading = false;
        Object.assign(note, newNote);
        this.$store.commit('onChangeNote', null);
      }
    }
  },
  created() {
    // todo
    this.initNotes()
  },
  data() {
    return {
      selectedNoteToDrag: null,
      notes: [],
      loading: true,
    }
  }
}
</script>

<style scoped>
.container {
  flex: 1;
  overflow: scroll;
  padding: 0 8px;
}
.body {
  display: grid;
  grid-auto-columns: 1fr;
  gap: 6px;
}
.note {
  display: flex;
  justify-content: space-between;
  text-align: left;
  -webkit-border-radius: 8px;
  -moz-border-radius: 8px;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 18px;
  gap: 12px;

  user-select: none;

  box-shadow: rgba(0, 0, 0, 0.1) 2px 4px 6px;

  cursor: pointer;
}
.note span:last-child {
  flex-shrink: 0;
}
.loader {
  margin-top: 32px;
}
.note-loader {
  display: flex;
}
</style>
