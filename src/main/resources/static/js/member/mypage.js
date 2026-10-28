document.addEventListener("DOMContentLoaded", function () {
    const NAME_REGEX = /^([가-힣]{2,}|[A-Za-z]{2,})$/;
    const PASSWORD_REGEX = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
    const PHONE_REGEX = /^010-\d{4}-\d{4}$/;
    const NAME_ERROR_MESSAGE = "이름은 한글 또는 영어만 입력 가능합니다. (혼용 불가)";
    const PASSWORD_ERROR_MESSAGE = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.";

    const infoForm = document.getElementById("info-form");
    const nameInput = document.getElementById("name");
    const nameMsg = document.getElementById("name-msg");
    const phoneInput = document.getElementById("phone");
    const phoneMsg = document.getElementById("phone-msg");

    const passwordFields = document.getElementById("password-fields");
    const currentPasswordInput = document.getElementById("currentPassword");
    const currentPasswordMsg = document.getElementById("current-password-msg");
    const newPasswordInput = document.getElementById("newPassword");
    const newPasswordFormatMsg = document.getElementById("new-password-format-msg");
    const newPasswordConfirmInput = document.getElementById("newPasswordConfirm");
    const newPasswordMsg = document.getElementById("new-password-msg");

    const passwordToggleBtn = document.getElementById("password-toggle-btn");
    const passwordCancelBtn = document.getElementById("password-cancel-btn");

    const editStartBtn = document.getElementById("edit-start-btn");
    const editActions = document.getElementById("edit-actions");
    const editCancelBtn = document.getElementById("edit-cancel-btn");

    const profilePreview = document.getElementById("profile-preview");
    const profileActions = document.getElementById("profile-actions");
    const profileImageInput = document.getElementById("profileImage");
    const profileRemoveBtn = document.getElementById("profile-remove-btn");
    const removeProfileImageInput = document.getElementById("removeProfileImage");
    const profileImageMsg = document.getElementById("profile-image-msg");
    const PROFILE_IMAGE_EXTENSIONS = ["jpg", "jpeg", "png", "gif", "webp"];
    const PROFILE_IMAGE_MAX_SIZE = 5 * 1024 * 1024;
    let profilePreviewObjectUrl = null;

    const withdrawArea = document.getElementById("withdraw-area");
    const withdrawToggleBtn = document.getElementById("withdraw-toggle-btn");
    const withdrawFields = document.getElementById("withdraw-fields");
    const withdrawForm = document.getElementById("withdraw-form");
    const withdrawPasswordInput = document.getElementById("withdrawPassword");
    const withdrawPasswordMsg = document.getElementById("withdraw-password-msg");

    function setMsg(el, text, isError) {
        el.textContent = text;
        el.classList.toggle("error-msg", !!isError);
        el.classList.toggle("success-msg", !isError && !!text);
    }

    // 비밀번호 입력칸 초기화 후 닫기
    function closePasswordFields() {
        [currentPasswordInput, newPasswordInput, newPasswordConfirmInput].forEach(function (input) {
            input.value = "";
        });
        [currentPasswordMsg, newPasswordFormatMsg, newPasswordMsg].forEach(function (msg) {
            setMsg(msg, "", false);
        });
        passwordFields.hidden = true;
    }

    // 프로필 미리보기 변경 (이전에 만든 미리보기 URL 은 해제)
    function setProfilePreview(src, objectUrl) {
        if (profilePreviewObjectUrl) {
            URL.revokeObjectURL(profilePreviewObjectUrl);
        }
        profilePreviewObjectUrl = objectUrl || null;
        profilePreview.src = src;
    }

    // 프로필 사진 선택 - 형식/용량 확인 후 미리보기 (저장 버튼을 눌러야 실제 반영)
    profileImageInput.addEventListener("change", function () {
        const file = profileImageInput.files[0];
        if (!file) {
            return;
        }

        const extension = file.name.split(".").pop().toLowerCase();
        if (!PROFILE_IMAGE_EXTENSIONS.includes(extension) || !file.type.startsWith("image/")) {
            profileImageInput.value = "";
            setMsg(profileImageMsg, "jpg, png, gif, webp 이미지 파일만 업로드할 수 있습니다.", true);
            return;
        }

        if (file.size > PROFILE_IMAGE_MAX_SIZE) {
            profileImageInput.value = "";
            setMsg(profileImageMsg, "이미지는 5MB 이하만 업로드할 수 있습니다.", true);
            return;
        }

        const objectUrl = URL.createObjectURL(file);
        setProfilePreview(objectUrl, objectUrl);
        removeProfileImageInput.value = "false";
        profileRemoveBtn.hidden = false;
        setMsg(profileImageMsg, "", false);
    });

    // 기본 이미지로 - 선택한 파일을 비우고 저장 시 기존 사진 삭제
    profileRemoveBtn.addEventListener("click", function () {
        profileImageInput.value = "";
        setProfilePreview(profilePreview.dataset.default);
        removeProfileImageInput.value = "true";
        profileRemoveBtn.hidden = true;
        setMsg(profileImageMsg, "", false);
    });

    // 프로필 사진 변경 내용 되돌리기 (정보 수정 취소 시)
    function resetProfileImage() {
        profileImageInput.value = "";
        setProfilePreview(profilePreview.dataset.original);
        removeProfileImageInput.value = "false";
        profileRemoveBtn.hidden = profilePreview.dataset.custom !== "true";
        setMsg(profileImageMsg, "", false);
    }

    // 회원 탈퇴 입력칸 초기화 후 닫기 (링크만 남김)
    function closeWithdrawFields() {
        withdrawPasswordInput.value = "";
        setMsg(withdrawPasswordMsg, "", false);
        withdrawFields.hidden = true;
    }

    // 정보 수정 - 버튼 클릭 시 이름/전화번호 수정 가능 (수정 모드), 회원 탈퇴 링크도 이때만 노출
    editStartBtn.addEventListener("click", function () {
        nameInput.readOnly = false;
        phoneInput.readOnly = false;
        editStartBtn.hidden = true;
        editActions.hidden = false;
        passwordToggleBtn.hidden = false;
        withdrawArea.hidden = false;
        profileActions.hidden = false;
        nameInput.focus();
    });

    // 정보 수정 취소 - 원래 값으로 되돌리고 보기 모드로
    editCancelBtn.addEventListener("click", function () {
        nameInput.value = nameInput.dataset.original || "";
        phoneTailDigits = parseInitialTailDigits(phoneInput.dataset.original);
        phoneInput.value = PHONE_PREFIX + formatPhoneDigits(phoneTailDigits);
        setMsg(nameMsg, "", false);
        setMsg(phoneMsg, "", false);
        closePasswordFields();
        closeWithdrawFields();
        resetProfileImage();

        nameInput.readOnly = true;
        phoneInput.readOnly = true;
        passwordToggleBtn.hidden = true;
        withdrawArea.hidden = true;
        profileActions.hidden = true;
        editActions.hidden = true;
        editStartBtn.hidden = false;
    });

    // 비밀번호 변경 - 버튼 클릭 시 입력칸 열기
    passwordToggleBtn.addEventListener("click", function () {
        passwordToggleBtn.hidden = true;
        passwordFields.hidden = false;
        currentPasswordInput.focus();
    });

    // 비밀번호 변경 취소 - 입력값/메시지 초기화 후 닫기 (비밀번호는 변경하지 않음)
    passwordCancelBtn.addEventListener("click", function () {
        closePasswordFields();
        passwordToggleBtn.hidden = false;
    });

    // 이름 - 숫자 키 입력 자체를 차단
    nameInput.addEventListener("keydown", function (e) {
        if (e.ctrlKey || e.metaKey || e.altKey) {
            return;
        }
        if (/^[0-9]$/.test(e.key)) {
            e.preventDefault();
        }
    });

    nameInput.addEventListener("paste", function (e) {
        const clipboard = e.clipboardData || window.clipboardData;
        const pasted = clipboard ? clipboard.getData("text") : "";
        if (/[0-9]/.test(pasted)) {
            e.preventDefault();
        }
    });

    // 이름 형식 체크 (한글만 또는 영어만, 2자 이상, 혼용 불가)
    nameInput.addEventListener("input", function () {
        const name = nameInput.value.trim();
        if (name && !NAME_REGEX.test(name)) {
            setMsg(nameMsg, NAME_ERROR_MESSAGE, true);
        } else {
            setMsg(nameMsg, "", false);
        }
    });

    // 전화번호 - 010- 고정, 뒤 8자리만 입력 가능한 마스크 입력
    const PHONE_PREFIX = "010-";

    function formatPhoneDigits(digits) {
        return digits.length > 4 ? `${digits.slice(0, 4)}-${digits.slice(4)}` : digits;
    }

    function parseInitialTailDigits(value) {
        if (value && value.startsWith(PHONE_PREFIX)) {
            return value.slice(PHONE_PREFIX.length).replace(/[^0-9]/g, "").slice(0, 8);
        }
        const digits = (value || "").replace(/[^0-9]/g, "");
        return digits.startsWith("010") ? digits.slice(3, 11) : digits.slice(0, 8);
    }

    let phoneTailDigits = parseInitialTailDigits(phoneInput.value);

    function renderPhone() {
        phoneInput.value = PHONE_PREFIX + formatPhoneDigits(phoneTailDigits);

        if (phoneTailDigits.length > 0 && phoneTailDigits.length < 8) {
            setMsg(phoneMsg, "전화번호 8자리를 모두 입력해주세요.", true);
        } else if (phoneTailDigits.length === 8) {
            setMsg(phoneMsg, "", false);
        }
    }

    function moveCaretToEnd() {
        const len = phoneInput.value.length;
        phoneInput.setSelectionRange(len, len);
    }

    // 서버 검증 에러 메시지가 있으면 유지되도록 초기 렌더링 시에는 값만 맞춤
    phoneInput.value = PHONE_PREFIX + formatPhoneDigits(phoneTailDigits);

    phoneInput.addEventListener("focus", moveCaretToEnd);
    phoneInput.addEventListener("click", moveCaretToEnd);

    phoneInput.addEventListener("keydown", function (e) {
        // 보기 모드(읽기 전용)에서는 마스크 입력 처리 안 함
        if (phoneInput.readOnly || e.ctrlKey || e.metaKey || e.altKey) {
            return;
        }

        if (e.key === "Backspace" || e.key === "Delete") {
            e.preventDefault();
            phoneTailDigits = phoneTailDigits.slice(0, -1);
            renderPhone();
            moveCaretToEnd();
            return;
        }

        if (/^[0-9]$/.test(e.key)) {
            e.preventDefault();
            if (phoneTailDigits.length < 8) {
                phoneTailDigits += e.key;
                renderPhone();
                moveCaretToEnd();
            }
            return;
        }

        if (["ArrowLeft", "ArrowRight", "Tab", "Home", "End", "Shift", "Enter"].includes(e.key)) {
            return;
        }

        e.preventDefault();
    });

    phoneInput.addEventListener("paste", function (e) {
        e.preventDefault();
        if (phoneInput.readOnly) {
            return;
        }
        const pasted = (e.clipboardData || window.clipboardData).getData("text");
        const digits = pasted.replace(/[^0-9]/g, "");
        phoneTailDigits = (phoneTailDigits + digits).slice(0, 8);
        renderPhone();
        moveCaretToEnd();
    });

    // 정보 수정 - 이름/전화번호 + (입력한 경우) 비밀번호까지 함께 검증
    infoForm.addEventListener("submit", function (e) {
        const name = nameInput.value.trim();
        const phone = phoneInput.value.trim();
        const currentPw = currentPasswordInput.value;
        const pw = newPasswordInput.value;
        const pwConfirm = newPasswordConfirmInput.value;
        let hasError = false;

        if (!NAME_REGEX.test(name)) {
            setMsg(nameMsg, NAME_ERROR_MESSAGE, true);
            hasError = true;
        }

        if (!PHONE_REGEX.test(phone)) {
            setMsg(phoneMsg, "전화번호를 010-XXXX-XXXX 형식으로 모두 입력해주세요.", true);
            hasError = true;
        }

        // 새 비밀번호를 입력한 경우에만 비밀번호 변경
        if (pw || pwConfirm) {
            if (!currentPw) {
                setMsg(currentPasswordMsg, "현재 비밀번호를 입력해주세요.", true);
                hasError = true;
            }

            if (!PASSWORD_REGEX.test(pw)) {
                setMsg(newPasswordFormatMsg, PASSWORD_ERROR_MESSAGE, true);
                hasError = true;
            }

            if (pw !== pwConfirm) {
                setMsg(newPasswordMsg, "새 비밀번호가 일치하지 않습니다.", true);
                hasError = true;
            }
        }

        if (hasError) {
            e.preventDefault();
        }
    });

    // 새 비밀번호 형식 체크
    newPasswordInput.addEventListener("input", function () {
        const pw = newPasswordInput.value;
        if (!pw) {
            setMsg(newPasswordFormatMsg, "", false);
        } else if (!PASSWORD_REGEX.test(pw)) {
            setMsg(newPasswordFormatMsg, PASSWORD_ERROR_MESSAGE, true);
        } else {
            setMsg(newPasswordFormatMsg, "사용 가능한 비밀번호입니다.", false);
        }
        checkPasswordMatch();
    });

    // 새 비밀번호 확인 일치 여부
    newPasswordConfirmInput.addEventListener("input", checkPasswordMatch);

    function checkPasswordMatch() {
        const pw = newPasswordInput.value;
        const pwConfirm = newPasswordConfirmInput.value;

        if (pwConfirm && pw !== pwConfirm) {
            setMsg(newPasswordMsg, "새 비밀번호가 일치하지 않습니다.", true);
        } else if (pwConfirm) {
            setMsg(newPasswordMsg, "새 비밀번호가 일치합니다.", false);
        } else {
            setMsg(newPasswordMsg, "", false);
        }
    }

    currentPasswordInput.addEventListener("input", function () {
        setMsg(currentPasswordMsg, "", false);
    });

    // 커플 연결 해제 - 확인 창
    const disconnectForm = document.getElementById("disconnect-form"); // 커플 연결 시에만 존재
    if (disconnectForm) {
        disconnectForm.addEventListener("submit", function (e) {
            const ok = confirm("커플 연결을 해제하시겠습니까?\n해제하면 함께 기록한 일정, 지출, 추억을 더 이상 볼 수 없습니다.");
            if (!ok) {
                e.preventDefault();
            }
        });
    }

    // 회원 탈퇴 - 링크 클릭 시 비밀번호 재확인 칸 열기 / 다시 클릭 시 닫기
    withdrawToggleBtn.addEventListener("click", function (e) {
        e.preventDefault();
        if (withdrawFields.hidden) {
            withdrawFields.hidden = false;
            withdrawPasswordInput.focus();
        } else {
            closeWithdrawFields();
        }
    });

    // 입력칸/버튼은 form="withdraw-form" 으로 연결되어 있어 submit 이벤트는 탈퇴 폼에서 발생
    withdrawForm.addEventListener("submit", function (e) {
        if (!withdrawPasswordInput.value) {
            e.preventDefault();
            setMsg(withdrawPasswordMsg, "비밀번호를 입력해주세요.", true);
            return;
        }

        const ok = confirm("정말 탈퇴하시겠습니까?\n탈퇴 후에는 계정을 복구할 수 없습니다.");
        if (!ok) {
            e.preventDefault();
        }
    });
});
