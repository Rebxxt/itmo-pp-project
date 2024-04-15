<template>
  <div>
    <form @submit.prevent="onCreate">
      <input
          v-model="text"
          placeholder="Сделать домашнее задание..."
      >
      <button type="submit">Добавить</button>
    </form>
  </div>
</template>

<script>
import {MIN_NOTE_LENGTH} from "@/components/js/types";
import {uuid} from "vue-uuid";

export default {
  name: 'NoteCreator',
  data() {
    return {
      text: ''
    }
  },
  methods: {
    onCreate() {
      if (this.text.length < MIN_NOTE_LENGTH) {
        console.error('length should be more than', MIN_NOTE_LENGTH)
        return
      }
      const note = {
        id: uuid.v4(), // TODO: remove after connect backend
        text: this.text,
        createdAt: new Date(),
      }
      this.text = '';
      this.$emit('onCreateNote', note)
    },
  }
}
</script>

<style scoped>
form {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
input {
  flex: 1;
  padding: 8px;
  font-size: 16px;
}
</style>
