import {monthNames} from "@/components/js/types";

export const isSameDate = (a, b) => {
    return a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();
}
export const getMonthName = (month) => {
    return [
        monthNames.MONTH_JANUARY,
        monthNames.MONTH_FEBRUARY,
        monthNames.MONTH_MARCH,
        monthNames.MONTH_APRIL,
        monthNames.MONTH_MAY,
        monthNames.MONTH_JUNE,
        monthNames.MONTH_JULY,
        monthNames.MONTH_AUGUST,
        monthNames.MONTH_SEPTEMBER,
        monthNames.MONTH_OCTOBER,
        monthNames.MONTH_NOVEMBER,
        monthNames.MONTH_DECEMBER,
    ][month];
}

export const getDaysInMonth = (year, month) => {
    return new Date(year, month + 1, 0).getDate();
}

export const dateDiff = (from, to) => {
    return Math.round((to - from) / (1000 * 60 * 60 * 24));
}
