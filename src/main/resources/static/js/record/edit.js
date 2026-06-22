document.addEventListener('DOMContentLoaded', function () {
    const imageInput = document.getElementById('images');
    const mainPreview = document.getElementById('mainPreview');
    const uploadPlus = document.getElementById('uploadPlus');
    const uploadText = document.getElementById('uploadText');
    const thumbnailList = document.getElementById('thumbnailList');

    if (!imageInput) {
        return;
    }

    imageInput.addEventListener('change', function () {
        const files = Array.from(imageInput.files);

        if (files.length === 0) {
            return;
        }

        const firstFile = files[0];
        const mainReader = new FileReader();

        mainReader.onload = function (event) {
            mainPreview.src = event.target.result;
            mainPreview.style.display = 'block';

            if (uploadPlus) uploadPlus.style.display = 'none';
            if (uploadText) uploadText.style.display = 'none';
        };

        mainReader.readAsDataURL(firstFile);

        thumbnailList.innerHTML = '';

        files.forEach(function (file) {
            const reader = new FileReader();

            reader.onload = function (event) {
                const thumbnail = document.createElement('div');
                thumbnail.className = 'thumbnail-item';

                const img = document.createElement('img');
                img.src = event.target.result;
                img.alt = '새로 선택한 사진';

                thumbnail.appendChild(img);
                thumbnailList.appendChild(thumbnail);
            };

            reader.readAsDataURL(file);
        });
    });
});