document.addEventListener("DOMContentLoaded", function () {
    const inviteCodeElement = document.getElementById("invite-code");
    const inviteCode = inviteCodeElement ? inviteCodeElement.textContent.trim() : "";

    const copyBtn = document.getElementById("copy-btn");
    const kakaoShareBtn = document.getElementById("kakao-share-btn");

    const qrBtn = document.getElementById("qr-share-btn");
    const qrModal = document.getElementById("qr-modal");
    const qrCloseBtn = document.getElementById("qr-close-btn");

    const inviteUrl =
        `${window.location.origin}/couple/connect?code=${inviteCode}`;

    // 코드 복사
    if (copyBtn) {
        copyBtn.addEventListener("click", function () {
            navigator.clipboard.writeText(inviteCode)
                .then(function () {
                    alert("초대 코드가 복사되었습니다.");
                })
                .catch(function () {
                    alert("초대 코드가 복사되지 않았습니다.");
                })
        });
    }

    // 카카오톡 공유
    kakaoShareBtn.addEventListener("click", function () {
        if (!window.Kakao) {
            alert("카카오톡을 불러오지 못했습니다. 다시 시도해주세요.");
            return;
        }

        if (!Kakao.isInitialized()) {
            Kakao.init("여기에_카카오_자바스크립트_키");
        }

        Kakao.Share.sendDefault({
            objectType: "text",
            text: `Two-Gether에서 함께 기록해요.\n초대 코드: ${inviteCode}`,
            link: {
                mobileWebUrl: inviteUrl,
                webUrl: inviteUrl
            }
        });
    });

    // QR 버튼
    if (qrBtn && qrModal) {
        qrBtn.addEventListener("click", function () {
            qrModal.style.display = "flex";
            console.log("QR 열기");
        });
    }

    if (qrCloseBtn && qrModal) {
        qrCloseBtn.addEventListener("click", function () {
            qrModal.style.display = "none";
            console.log("닫기 버튼 클릭");
        });
    }
});