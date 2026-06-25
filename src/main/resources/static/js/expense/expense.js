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

    modal.classList.add('show');
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
    document.getElementById('detailPaidBy').textContent = selectedExpense.paidBy;
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
    document.getElementById('editAmount').value = selectedExpense.amount;
    document.getElementById('editMemo').value = selectedExpense.memo;
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