document.addEventListener('DOMContentLoaded', function () {
    const calendarElement = document.getElementById('calendar');
    const selectedDateElement = document.getElementById('selectedDate');
    const scheduleListElement = document.getElementById('scheduleList');

    const openModalBtn = document.getElementById('openModalBtn');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const cancelModalBtn = document.getElementById('cancelModalBtn');
    const scheduleModal = document.getElementById('scheduleModal');

    const modalTitle = document.getElementById('modalTitle');
    const scheduleForm = document.getElementById('scheduleForm');
    const deleteForm = document.getElementById('deleteForm');
    const deleteBtn = document.getElementById('deleteBtn');

    const scheduleDateInput = document.getElementById('scheduleDateInput');
    const titleInput = document.getElementById('title');
    const memoInput = document.getElementById('memo');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');
    const useTimeCheckbox = document.getElementById('useTimeCheckbox');
    const timeFields = document.getElementById('timeFields');

    let selectedDate = getToday();

    const events = schedules.map(schedule => ({
        id: schedule.id,
        title: schedule.title,
        start: schedule.scheduleDate,
        memo: schedule.memo,
        startTime: schedule.startTime,
        endTime: schedule.endTime
    }));

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
            selectedDateElement.textContent = formatDateTitle(selectedDate);
            renderDailySchedules(selectedDate);
        },

        eventClick: function (info) {
            const event = events.find(e => String(e.id) === String(info.event.id));

            if (!event) {
                return;
            }

            selectedDate = event.start;
            selectedDateElement.textContent = formatDateTitle(selectedDate);
            renderDailySchedules(selectedDate);
            openEditModal(event);
        },

        events: events

    });

    calendar.render();

    selectedDateElement.textContent = formatDateTitle(selectedDate);
    renderDailySchedules(selectedDate);

    openModalBtn.addEventListener('click', function () {
        openCreateModal();
    });

    closeModalBtn.addEventListener('click', closeModal);
    cancelModalBtn.addEventListener('click', closeModal);

    function closeModal() {
        scheduleModal.classList.add('hidden');
    }

    function renderDailySchedules(date) {
        const dailySchedules = events.filter(event => event.start === date);

        if (dailySchedules.length === 0) {
            scheduleListElement.innerHTML =
                '<p class="empty-text">등록된 일정이 없습니다.</p>';
            return;
        }

        scheduleListElement.innerHTML = dailySchedules.map(schedule => {
            const timeText = schedule.startTime
                ? `<p class="schedule-time">${schedule.startTime}${schedule.endTime ? ' - ' + schedule.endTime : ''}</p>`
                : '';

            const memoText = schedule.memo
                ? `<p class="schedule-memo">${schedule.memo}</p>`
                : '';

            return `
                <button type="button"
                        class="schedule-item-card"
                        data-id="${schedule.id}">
                    <div class="schedule-title-row">
                        <span class="schedule-check"></span>
                        <strong>${schedule.title}</strong>
                    </div>
                    ${timeText}
                    ${memoText}
                </button>
            `;
        }).join('');

        document.querySelectorAll('.schedule-item-card').forEach(item => {
            item.addEventListener('click', function () {
                const scheduleId = this.dataset.id;
                const schedule = events.find(event => String(event.id) === String(scheduleId));

                if (schedule) {
                    openEditModal(schedule);
                }
            });
        });
    }

    function openCreateModal() {
        modalTitle.textContent = '일정 추가';

        scheduleForm.action = '/calendar';
        deleteForm.action = '';
        deleteBtn.style.display = 'none';

        scheduleDateInput.value = selectedDate;
        titleInput.value = '';
        memoInput.value = '';
        startTimeInput.value = '';
        endTimeInput.value = '';
        useTimeCheckbox.checked = false;
        timeFields.classList.add('hidden');

        scheduleModal.classList.remove('hidden');
    }

    function openEditModal(schedule) {
        modalTitle.textContent = schedule.title;

        scheduleForm.action = `/calendar/${schedule.id}/edit`;
        deleteForm.action = `/calendar/${schedule.id}/delete`;
        deleteBtn.style.display = 'block';

        scheduleDateInput.value = schedule.start;
        titleInput.value = schedule.title;
        memoInput.value = schedule.memo || '';
        startTimeInput.value = schedule.startTime || '';
        endTimeInput.value = schedule.endTime || '';
        if (schedule.startTime || schedule.endTime) {
            useTimeCheckbox.checked = true;
            timeFields.classList.remove('hidden');
        } else {
            useTimeCheckbox.checked = false;
            timeFields.classList.add('hidden');
        }

        scheduleModal.classList.remove('hidden');
    }

    function closeModal() {
        scheduleModal.classList.add('hidden');
    }

    function getToday() {
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const date = String(today.getDate()).padStart(2, '0');

        return `${year}-${month}-${date}`;
    }

    function formatDateTitle(date) {
        const [year, month, day] = date.split('-').map(Number);
        const dateObject = new Date(year, month - 1, day);
        const dayNames = ['일', '월', '화', '수', '목', '금', '토'];

        return `${month}.${day} (${dayNames[dateObject.getDay()]}) 일정`;
    }

    useTimeCheckbox.addEventListener('change', function () {
        if (this.checked) {
            timeFields.classList.remove('hidden');
        } else {
            timeFields.classList.add('hidden');
            startTimeInput.value = '';
            endTimeInput.value = '';
        }
    });
});