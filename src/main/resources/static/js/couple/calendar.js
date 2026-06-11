document.addEventListener('DOMContentLoaded', function () {
   const calendarElement = document.getElementById('calendar');
   const selectedDateElement = document.getElementById('selectedDate');

   const calendar = new FullCalendar.Calendar(calendarElement, {
       initialView: 'dayGridMonth',
       locale: 'ko',
       height: 680,

       headerToolbar: {
           left: 'prev,next today',
           center: 'title'
       },

       buttonText: {
           today: '오늘'
       },

       dateClick: function (info) {
           selectedDateElement.textContent = info.dateStr;
       },

       events: [
           {
               title: '양평 데이트',
               start: '2026-06-18'
           },
           {
               title: '기념일',
               start: '2026-06-25'
           }
       ]
   });

    calendar.render();
});