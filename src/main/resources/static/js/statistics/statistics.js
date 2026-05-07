document.addEventListener("DOMContentLoaded", () => {
    categoryChart();
    monthlyChart();
});

function categoryChart() {
    const canvas = document.getElementById("categoryChart");

    if (!canvas || !categoryExpenses || categoryExpenses.length === 0) {
        return;
    }

    const labels = categoryExpenses.map(item => item.category);
    const amounts = categoryExpenses.map(item => item.amount);

    new Chart(canvas, {
        type: "pie",
        data: {
            labels: labels,
            datasets: [
                {
                    data: amounts
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: "bottom"
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const label = context.label || "";
                            const value = context.raw || 0;
                            return `${label}: ${value.toLocaleString()}원`;
                        }
                    }
                }
            }
        }
    });
}

function monthlyChart() {
    const canvas = document.getElementById("monthlyChart");

    if (!canvas || !monthlyExpenses || monthlyExpenses.length === 0) {
        return;
    }

    const labels = monthlyExpenses.map(item => item.month);
    const amounts = monthlyExpenses.map(item => item.amount);

    new Chart(canvas, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "월별 지출",
                    data: amounts
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value) {
                            return value.toLocaleString() + "원";
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const value = context.raw || 0;
                            return `${value.toLocaleString()}원`;
                        }
                    }
                }
            }
        }
    });
}