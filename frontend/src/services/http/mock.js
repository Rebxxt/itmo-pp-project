import {uuid} from "vue-uuid";

const currentDate = new Date()

export const initNotes = [{
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
},{
    id: uuid.v4(),
    text: 'jsfklda ljkfsda lkjjklfsad jklfsda jklkljsfad jlkf 123123123',
    createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 30, currentDate.getHours(), currentDate.getMinutes() - 43),
},{
    id: uuid.v4(),
    text: 'jsfklda ljkfsda  ljksf adljkjklfs adjklf sad',
    createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 29, currentDate.getHours(), currentDate.getMinutes() - 43),
},{
    id: uuid.v4(),
    text: 'jsfklda ljkfsda lkjjklfsad jklfsda jklkljsfad jlkf 99999999999',
    createdAt: new Date(currentDate.getFullYear(), currentDate.getMonth(), 31, currentDate.getHours(), currentDate.getMinutes() - 43),
},
]
