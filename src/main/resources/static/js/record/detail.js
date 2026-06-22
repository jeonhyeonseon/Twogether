document.addEventListener('DOMContentLoaded', function () {
    const mainImage = document.getElementById('mainImage');
    const thumbnailImages = document.querySelectorAll('.thumbnail-image');

    if (!mainImage) {
        return;
    }

    thumbnailImages.forEach(function (thumbnail) {
        thumbnail.addEventListener('click', function () {
            mainImage.src = thumbnail.src;
        });
    });
});