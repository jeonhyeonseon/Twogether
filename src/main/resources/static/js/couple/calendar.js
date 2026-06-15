document.addEventListener('DOMContentLoaded', function () {
    const calendarElement = document.getElementById('calendar');
    const selectedDateElement = document.getElementById('selectedDate');

    const openModalBtn = document.getElementById('openModalBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelModalBtn = document.getElementById('cancelModalBtn');
    const scheduleModal = document.getElementById('scheduleModal');
    const scheduleDateInput = document.getElementById('scheduleDateInput');

    let selectedDate = null;

    const events = schedules.map(schedule => {
        return {
            id: schedule.id,
            title: schedule.title,
            start: schedule.scheduleDate
        };
    });

    const calendar = new FullCalendar.Calendar(calendarElement, {
       initialView: 'dayGridMonth',
       locale: 'ko',
       height: 680,

       headerToolbar: {
           left: 'prev,next today',
           center: 'title',
           right: ''
       },

       buttonText: {
           today: '오늘'
       },

        dateClick: function (info) {
            selectedDate = info.dateStr;
            selectedDateElement.textContent = selectedDate;
        },

        events: events

    });

    calendar.render();

    openModalBtn.addEventListener('click', function () {
        if (selectedDate) {
            scheduleDateInput.value = selectedDate;
        } else {
            scheduleDateInput.value = '';
        }

        scheduleModal.classList.remove('hidden');
    });

    closeModalBtn.addEventListener('click', closeModal);
    cancelModalBtn.addEventListener('click', closeModal);

    function closeModal() {
        scheduleModal.classList.add('hidden');
    }
});