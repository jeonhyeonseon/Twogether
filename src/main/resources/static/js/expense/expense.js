let selectedExpense = null;

function openCreateModal() {
    const modal = document.getElementById('expenseModal');
    const form = document.getElementById('expenseForm');

    document.getElementById('modalTitle').textContent = '비용 등록';
    document.getElementById('submitBtn').textContent = '등록하기';

    form.action = '/expense';
    form.reset();

    modal.classList.add('show');
}

function openEditModal(button) {
    const modal = document.getElementById('expenseModal');
    const form = document.getElementById('expenseForm');

    const id = button.dataset.id;
    const date = button.dataset.date;
    const content = button.dataset.content;
    const category = button.dataset.category;
    const amount = button.dataset.amount;
    const memo = button.dataset.memo;

    document.getElementById('modalTitle').textContent = '비용 수정';
    document.getElementById('submitBtn').textContent = '수정하기';

    form.action = `/expense/${id}/edit`;

    document.getElementById('expenseDate').value = date;
    document.getElementById('content').value = content;
    document.getElementById('category').value = category;
    document.getElementById('amount').value = amount;
    document.getElementById('memo').value = memo || '';

    modal.classList.add('show');
}

function closeExpenseModal() {
    document.getElementById('expenseModal').classList.remove('show');
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

    document.getElementById('detailContent').textContent = selectedExpense.content;
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

    document.getElementById('expenseDetailModal').classList.add('show');
}

function closeDetailModal() {
    document.getElementById('expenseDetailModal').classList.remove('show');
}

function openEditModalFromDetail() {
    if (!selectedExpense) {
        return;
    }

    closeDetailModal();

    const fakeButton = {
        dataset: {
            id: selectedExpense.id,
            date: selectedExpense.date,
            content: selectedExpense.content,
            category: selectedExpense.category,
            amount: selectedExpense.amount,
            memo: selectedExpense.memo
        }
    };

    openEditModal(fakeButton);
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