<template>
  <div>
    <form @submit.prevent="onCreate">
      <input
          v-model="text"
          placeholder="Сделать домашнее задание..."
          :disabled="loading"
      >
      <button type="submit" :disabled="loading">Добавить</button>
    </form>
    <p class="validator" v-if="validator">{{validator}}</p>
  </div>
</template>

<script>
import {MIN_NOTE_LENGTH, VALIDATOR_LENGTH_MESSAGE} from "@/components/js/types";

export default {
  name: 'NoteCreator',
  props: {
    loading: Boolean
  },
  data() {
    return {
      text: '',
      validator: null
    }
  },
  computed: {
    selectedDay() {
      return this.$store.state.selectedDay
    }
  },
  methods: {
    onCreate() {
      if (this.text.length < MIN_NOTE_LENGTH) {
        console.error('length should be more than', MIN_NOTE_LENGTH)
        this.validator = VALIDATOR_LENGTH_MESSAGE;
        setTimeout(() => {
          this.validator = null;
        }, 5000)
        return
      }
      this.validator = null;
      const note = {
        text: this.text,
        createdAt: (new Date(this.selectedDay?.date ?? new Date())),
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
.validator {
  color: red;
}
</style>
