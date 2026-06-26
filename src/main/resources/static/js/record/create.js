document.addEventListener('DOMContentLoaded', function () {
    const imageInput = document.getElementById('images');
    const mainPreview = document.getElementById('mainPreview');
    const uploadPlus = document.getElementById('uploadPlus');
    const uploadText = document.getElementById('uploadText');
    const thumbnailList = document.getElementById('thumbnailList');

    imageInput.addEventListener('change', function () {
        const files = Array.from(imageInput.files);

        thumbnailList.innerHTML = '';

        if (files.length === 0) {
            mainPreview.removeAttribute('src');
            mainPreview.style.display = 'none';
            uploadPlus.style.display = 'block';
            uploadText.style.display = 'block';

            renderEmptyThumbnails(3);
            return;
        }

        const firstFile = files[0];
        const mainReader = new FileReader();

        mainReader.onload = function (event) {
            mainPreview.src = event.target.result;
            mainPreview.style.display = 'block';

            uploadPlus.style.display = 'none';
            uploadText.style.display = 'none';
        };

        mainReader.readAsDataURL(firstFile);

        files.forEach(function (file) {
            const reader = new FileReader();

            reader.onload = function (event) {
                const thumbnail = document.createElement('div');
                thumbnail.className = 'thumbnail';

                const img = document.createElement('img');
                img.src = event.target.result;
                img.alt = '선택한 사진';

                thumbnail.appendChild(img);
                thumbnailList.appendChild(thumbnail);
            };

            reader.readAsDataURL(file);
        });

        const emptyCount = Math.max(0, 3 - files.length);
        renderEmptyThumbnails(emptyCount);
    });

    function renderEmptyThumbnails(count) {
        for (let i = 0; i < count; i++) {
            const emptyThumbnail = document.createElement('div');
            emptyThumbnail.className = 'thumbnail empty';
            emptyThumbnail.textContent = '+';

            emptyThumbnail.addEventListener('click', function () {
                imageInput.click();
            });

            thumbnailList.appendChild(emptyThumbnail);
        }
    }
});