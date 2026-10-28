document.addEventListener("DOMContentLoaded", function () {
    const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const PASSWORD_REGEX = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
    const PASSWORD_ERROR_MESSAGE = "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.";

    function setMsg(el, text, isError) {
        el.textContent = text;
        el.classList.toggle("error-msg", !!isError);
        el.classList.toggle("success-msg", !isError && !!text);
    }

    function csrfHeaders(form) {
        const csrfInput = form.querySelector('input[name="_csrf"]');
        return csrfInput ? { "X-CSRF-TOKEN": csrfInput.value } : {};
    }

    // 아이디 찾기 / 비밀번호 찾기 - 인증코드 발송
    const findForm = document.querySelector(".find-form");
    if (findForm) {
        const sendCodeBtn = document.getElementById("send-code");
        const sendMsg = document.getElementById("send-msg");
        const emailInput = document.getElementById("email");
        const loginIdInput = document.getElementById("loginId"); // 비밀번호 찾기에만 존재

        sendCodeBtn.addEventListener("click", function () {
            const email = emailInput.value.trim();
            const loginId = loginIdInput ? loginIdInput.value.trim() : null;

            if (loginIdInput && !loginId) {
                alert("아이디를 입력해주세요.");
                return;
            }

            if (!email) {
                alert("이메일을 입력해주세요.");
                return;
            }

            if (!EMAIL_REGEX.test(email)) {
                setMsg(sendMsg, "이메일 형식이 올바르지 않습니다.", true);
                return;
            }

            let body = `email=${encodeURIComponent(email)}`;
            if (loginIdInput) {
                body += `&loginId=${encodeURIComponent(loginId)}`;
            }

            sendCodeBtn.disabled = true;

            fetch(findForm.dataset.sendUrl, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded", ...csrfHeaders(findForm) },
                body: body
            })
                .then(function (response) {
                    // 서버 오류(5xx)는 응답 본문 대신 사용자용 메시지 표시
                    if (response.status >= 500) {
                        throw new Error("server error");
                    }
                    return response.text().then(function (text) {
                        return { ok: response.ok, text: text };
                    });
                })
                .then(function (result) {
                    setMsg(sendMsg, result.text, !result.ok);
                })
                .catch(function () {
                    setMsg(sendMsg, "인증코드 발송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", true);
                })
                .finally(function () {
                    sendCodeBtn.disabled = false;
                });
        });
    }

    // 비밀번호 재설정 - 형식/일치 체크
    const resetForm = document.querySelector(".reset-form");
    if (resetForm) {
        const newPasswordInput = document.getElementById("newPassword");
        const newPasswordFormatMsg = document.getElementById("new-password-format-msg");
        const newPasswordConfirmInput = document.getElementById("newPasswordConfirm");
        const newPasswordMsg = document.getElementById("new-password-msg");

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

        newPasswordConfirmInput.addEventListener("input", checkPasswordMatch);

        resetForm.addEventListener("submit", function (e) {
            const pw = newPasswordInput.value;
            const pwConfirm = newPasswordConfirmInput.value;
            let hasError = false;

            if (!PASSWORD_REGEX.test(pw)) {
                setMsg(newPasswordFormatMsg, PASSWORD_ERROR_MESSAGE, true);
                hasError = true;
            }

            if (pw !== pwConfirm) {
                setMsg(newPasswordMsg, "새 비밀번호가 일치하지 않습니다.", true);
                hasError = true;
            }

            if (hasError) {
                e.preventDefault();
            }
        });
    }
});
