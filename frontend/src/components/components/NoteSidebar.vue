<template>
  <div class="container">
    <div class="body">
      <h4>Notes</h4>
      <div
          class="note"
          v-for="(note, index) in notes"
          :key="index"
      >{{note.title}}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NoteSidebar',
  data() {
    return {
      notes: [{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      },{
        title: 'test'
      }]
    }
  }
}
</script>

<style scoped>
.container {
  flex: auto;
  overflow: scroll;
}
.body {
  display: grid;
  grid-auto-columns: 1fr;
  gap: 6px;
}
.note {box-shadow: rgba(0, 0, 0, 0.1) 2px 4px 6px;
  -webkit-border-radius: 8px;
  -moz-border-radius: 8px;
  border-radius: 8px;
  padding: 4px 8px;
}
</style>
