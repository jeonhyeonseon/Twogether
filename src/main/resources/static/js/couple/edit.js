function previewImage(input) {
    const file = input.files[0];

    if (!file) {
        return;
    }

    const img = input.closest('.profile-upload')
        .querySelector('.couple-profile-img');

    img.src = URL.createObjectURL(file);
}

document.querySelectorAll('input[type="file"]').forEach(input => {
    input.addEventListener('change', function () {
        previewImage(this);
    });
});