document.addEventListener('DOMContentLoaded', function () {
    const mainImage = document.getElementById('mainImage');
    const thumbnailImages = document.querySelectorAll('.thumbnail-image');
    const thumbnails = document.querySelectorAll('.thumbnail');
    const prevButton = document.querySelector('.photo-prev');
    const nextButton = document.querySelector('.photo-next');

    if (!mainImage || thumbnailImages.length === 0) {
        return;
    }

    const imageUrls = Array.from(thumbnailImages).map(function (image) {
        return image.getAttribute('src');
    });

    let currentIndex = 0;

    function updateMainImage(index) {
        currentIndex = index;
        mainImage.src = imageUrls[currentIndex];

        thumbnails.forEach(function (thumbnail) {
            thumbnail.classList.remove('active');
        });

        if (thumbnails[currentIndex]) {
            thumbnails[currentIndex].classList.add('active');
        }
    }

    thumbnails.forEach(function (thumbnail) {
        thumbnail.addEventListener('click', function () {
            const index = Number(thumbnail.dataset.index);
            updateMainImage(index);
        });
    });

    if (prevButton) {
        prevButton.addEventListener('click', function () {
            const prevIndex = currentIndex === 0
                ? imageUrls.length - 1
                : currentIndex - 1;

            updateMainImage(prevIndex);
        });
    }

    if (nextButton) {
        nextButton.addEventListener('click', function () {
            const nextIndex = currentIndex === imageUrls.length - 1
                ? 0
                : currentIndex + 1;

            updateMainImage(nextIndex);
        });
    }

    updateMainImage(0);
});