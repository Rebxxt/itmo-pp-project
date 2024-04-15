<template>
  <div class="container">
    <div class="body">
      <h4 style="margin-top: 0;">Notes</h4>

      <NoteCreator @onCreateNote="addNote"></NoteCreator>

      <div class="list" v-if="filteredNotes.length > 0">
        <div
            :draggable="true"
            @dragstart="startDrag($event, note)"
            @dragend="endDrag"
            class="note"
            v-for="(note, index) in filteredNotes"
            :key="index"
        >
          <span>{{note.text}}</span>

          <span>{{note.createdAt?.toLocaleString('ru')}}</span>
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
import {uuid} from "vue-uuid";

export default {
  name: 'NoteSidebar',
  components: {NoteCreator},
  props: {
    selectedDay: {
      date: Date,
    },
  },
  methods: {
    addNote(note) {
      if (this.selectedDay) {
        note.createdAt.setDate(this.selectedDay.date.getDate());
      }
      this.notes.push(note)
    },
    startDrag(e, note) {
      this.selectedNoteToDrag = note;
      this.$store.commit('onSelectedNote', note)
    },
    endDrag() {
      this.selectedNoteToDrag = null;
      this.$store.commit('onSelectedNote', null)
    }
  },
  computed: {
    filteredNotes() {
      const reversed = [...this.notes]
      reversed.sort((a, b) => b.createdAt-a.createdAt)
      if (this.selectedDay) {
        console.log(this.selectedDay.date, reversed)
        return reversed.filter(v => v.createdAt.getDate() === this.selectedDay.date.getDate())
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
        const note = this.notes.find(v => v.id === newNote.id)
        console.log('note', note)
        Object.assign(note, newNote)
        console.log(this.notes)
        this.notes = [...this.notes]
        this.$store.commit('onChangeNote', null)
      }
    }
  },
  data() {
    const currentDate = new Date()
    return {
      selectedNoteToDrag: null,
      notes: [{
        id: uuid.v4(),
        text: 'Note about family',
        createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 1, currentDate.getHours(), currentDate.getMinutes()),
      },{
        id: uuid.v4(),
        text: 'Note about animals',
        createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 3, currentDate.getHours() - 10, currentDate.getMinutes() - 23),
      },{
        id: uuid.v4(),
        text: 'Note about work',
        createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 5, currentDate.getHours(), currentDate.getMinutes() - 20),
      },{
        id: uuid.v4(),
        text: 'Note about university',
        createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 8, currentDate.getHours(), currentDate.getMinutes() - 43),
      },{
        id: uuid.v4(),
        text: 'jsfklda ljkfsda lkjjklfsad jklfsda jklkljsfad jlkf dsaljkjfklsda ljkf dasjlkf sadljkf sdljakjklaf sdlkjfasd ljksf adljkjklfs adjklf sad',
        createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 8, currentDate.getHours(), currentDate.getMinutes() - 43),
      },
      ]
    }
  }
}
</script>

<style scoped>
.container {
  flex: auto;
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

  box-shadow: rgba(0, 0, 0, 0.1) 2px 4px 6px;
}
.note span:last-child {
  flex-shrink: 0;
}
</style>
