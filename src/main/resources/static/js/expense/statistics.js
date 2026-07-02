document.addEventListener('DOMContentLoaded', function () {
    const yearSelect = document.getElementById('yearSelect');
    const monthSelect = document.getElementById('monthSelect');
    const categorySelect = document.getElementById('categorySelect');
    const statisticsForm = document.getElementById('statisticsFilterForm');
    const selects = statisticsForm ? statisticsForm.querySelectorAll('select') : [];

    if (yearSelect && statisticsForm) {
        yearSelect.addEventListener('change', function () {
            statisticsForm.submit();
        });
    }

    if (monthSelect && statisticsForm) {
        monthSelect.addEventListener('change', function () {
            statisticsForm.submit();
        });
    }

    if (categorySelect && statisticsForm) {
        categorySelect.addEventListener('change', function () {
            statisticsForm.submit();
        });
    }

    selects.forEach(function (select) {
        select.addEventListener('change', function () {
            statisticsForm.submit();
        });
    });

});