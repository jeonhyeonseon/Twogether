let selectedExpense = null;

function openCreateModal() {
    const modal = document.getElementById('expenseModal');
    const form = document.getElementById('expenseForm');

    const connectRecordIdInput = document.getElementById('connectRecordId');
    const recordIdValue = connectRecordIdInput ? connectRecordIdInput.value : '';

    document.getElementById('modalTitle').textContent = '비용 등록';
    document.getElementById('submitBtn').textContent = '등록하기';

    form.action = '/expense';
    form.reset();

    const recordIdInput = form.querySelector('input[name="recordId"]');

    if (recordIdInput) {
        recordIdInput.value = recordIdValue;
    }

    const amountDisplay = document.getElementById('amountDisplay');
    const amount = document.getElementById('amount');

    if (amountDisplay) {
        amountDisplay.value = '';
    }

    if (amount) {
        amount.value = '';
    }

    modal.classList.add('show');
}

function closeExpenseModal() {
    document.getElementById('expenseModal').classList.remove('show');
}

function getTodayByKorea() {
    const now = new Date();

    const koreaDateText = now.toLocaleDateString('en-CA', {
        timeZone: 'Asia/Seoul'
    });

    return koreaDateText;
}

function openDetailModal(item) {
    selectedExpense = {
        id: item.dataset.id,
        date: item.dataset.date,
        content: item.dataset.content,
        category: item.dataset.category,
        categoryDisplay: item.dataset.categoryDisplay,
        amount: item.dataset.amount,
        paidBy: item.dataset.paidBy,
        memo: item.dataset.memo || ''
    };

    document.getElementById('detailTitle').textContent = selectedExpense.content;
    document.getElementById('detailDate').textContent = selectedExpense.date;
    document.getElementById('detailCategory').textContent = selectedExpense.categoryDisplay;

    const detailPaidBy = document.getElementById('detailPaidBy');

    if (detailPaidBy) {
        detailPaidBy.textContent = selectedExpense.paidBy;
    }

    document.getElementById('detailAmount').textContent =
        Number(selectedExpense.amount).toLocaleString() + '원';

    document.getElementById('detailMemo').textContent =
        selectedExpense.memo.trim() !== ''
            ? selectedExpense.memo
            : '등록된 메모가 없습니다.';

    document.getElementById('deleteForm').action =
        `/expense/${selectedExpense.id}/delete`;

    document.getElementById('detailEditForm').action =
        `/expense/${selectedExpense.id}/edit`;

    switchToViewMode();

    document.getElementById('expenseDetailModal').classList.add('show');
}

function switchToEditMode() {
    if (!selectedExpense) {
        return;
    }

    document.getElementById('detailViewArea').classList.add('hidden');
    document.getElementById('detailEditForm').classList.remove('hidden');

    document.getElementById('detailTitle').textContent = '비용 수정';

    document.getElementById('editExpenseDate').value = selectedExpense.date;
    document.getElementById('editContent').value = selectedExpense.content;
    document.getElementById('editCategory').value = selectedExpense.category;
    document.getElementById('editMemo').value = selectedExpense.memo;

    const editAmountDisplay = document.getElementById('editAmountDisplay');
    const editAmount = document.getElementById('editAmount');

    if (editAmountDisplay) {
        editAmountDisplay.value = Number(selectedExpense.amount).toLocaleString();
    }

    if (editAmount) {
        editAmount.value = selectedExpense.amount;
    }
}

function switchToViewMode() {
    document.getElementById('detailViewArea').classList.remove('hidden');
    document.getElementById('detailEditForm').classList.add('hidden');

    if (selectedExpense) {
        document.getElementById('detailTitle').textContent = selectedExpense.content;
    }
}

function closeDetailModal() {
    document.getElementById('expenseDetailModal').classList.remove('show');
    switchToViewMode();
}

function confirmDelete() {
    return confirm('비용을 삭제하시겠습니까?');
}

function formatAmountInput(displayInput, hiddenInput) {
    let value = displayInput.value;

    value = value.replace(/[^0-9]/g, '');

    if (value.length > 1 && value.startsWith('0')) {
        value = value.replace(/^0+/, '');
    }

    displayInput.value = value
        ? Number(value).toLocaleString()
        : '';

    hiddenInput.value = value;
}

function validateAmount(hiddenInput) {
    const amount = Number(hiddenInput.value);

    if (!amount || amount < 100) {
        alert('금액은 100원 이상부터 등록할 수 있습니다.');
        return false;
    }

    return true;
}

function getTodayByKorea() {
    const now = new Date();

    const koreaDateText = now.toLocaleDateString('en-CA', {
        timeZone: 'Asia/Seoul'
    });

    return koreaDateText;
}

function validateExpenseDate(dateInput) {
    if (!dateInput || !dateInput.value) {
        alert('날짜를 선택해주세요.');
        return false;
    }

    const today = getTodayByKorea();

    if (dateInput.value > today) {
        alert('미래 날짜로는 비용을 등록할 수 없습니다.');
        return false;
    }

    return true;
}

window.addEventListener('click', function (event) {
    const expenseModal = document.getElementById('expenseModal');
    const detailModal = document.getElementById('expenseDetailModal');

    if (event.target === expenseModal) {
        closeExpenseModal();
    }

    if (event.target === detailModal) {
        closeDetailModal();
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const amountDisplay = document.getElementById('amountDisplay');
    const amount = document.getElementById('amount');

    const editAmountDisplay = document.getElementById('editAmountDisplay');
    const editAmount = document.getElementById('editAmount');

    const expenseForm = document.getElementById('expenseForm');
    const detailEditForm = document.getElementById('detailEditForm');

    if (amountDisplay && amount) {
        amountDisplay.addEventListener('input', function () {
            formatAmountInput(amountDisplay, amount);
        });
    }

    if (editAmountDisplay && editAmount) {
        editAmountDisplay.addEventListener('input', function () {
            formatAmountInput(editAmountDisplay, editAmount);
        });
    }

    if (expenseForm) {
        expenseForm.addEventListener('submit', function (event) {
            if (!validateAmount(amount)) {
                event.preventDefault();
            }
        });
    }

    if (detailEditForm) {
        detailEditForm.addEventListener('submit', function (event) {
            if (!validateAmount(editAmount)) {
                event.preventDefault();
            }
        });
    }

    const connectRecordIdInput = document.getElementById('connectRecordId');

    if (!connectRecordIdInput) {
        return;
    }

    const connectRecordId = connectRecordIdInput.value;

    const connectModeInput = document.getElementById('connectMode');
    const connectMode = connectModeInput ? connectModeInput.value : '';

    if (connectRecordId && connectRecordId !== 'null' && connectMode === 'create') {
        openCreateModal();

        const dateRecordSelect = document.getElementById('dateRecordId');

        if (dateRecordSelect) {
            dateRecordSelect.value = connectRecordId;
        }
    }
});