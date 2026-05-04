document.addEventListener("DOMContentLoaded", function() {
    const typeSelect = document.getElementById("type");
    const categorySelect = document.getElementById("category");
    const amountInput = document.getElementById("amount");
    const form = document.querySelector(".transaction-container");

    function filterCategory() {
        const selectType = typeSelect.value;

        Array.from(categorySelect.options).forEach(option => {
            if (option.dataset.type === selectType) {
                option.style.display = "block";
            } else {
                option.style.display = "none";
            }
        });

        const firstVisible = Array.from(categorySelect.options).find(opt => opt.style.display !== "none");

        if (firstVisible) {
            categorySelect.value = firstVisible.value;
        }
    }

    typeSelect.addEventListener("change", filterCategory);
    filterCategory();

    amountInput.addEventListener("input", function () {
        let value = this.value.replace(/,/g, "").replace(/[^0-9]/g, "");

        if(!value) {
            this.value = "";
            return;
        }

        this.value = Number(value).toLocaleString();
    });

    form.addEventListener("submit", function () {
        amountInput.value = amountInput.value.replace(/,/g, "");
    });
})