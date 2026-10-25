document.addEventListener("DOMContentLoaded", function () {
    const NAME_REGEX = /^([가-힣]{2,}|[A-Za-z]{2,})$/;
    const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const PASSWORD_REGEX = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
    const PHONE_REGEX = /^010-\d{4}-\d{4}$/;
    const NAME_ERROR_MESSAGE = "이름은 한글 또는 영어만 입력 가능합니다. (혼용 불가)";

    let isLoginIdChecked = false;
    let isEmailVerified = false;

    const nameInput = document.getElementById("name");
    const nameMsg = document.getElementById("name-msg");

    const loginIdInput = document.getElementById("loginId");
    const loginIdGroup = document.getElementById("loginId-group");
    const loginIdCheckedInput = document.getElementById("loginIdChecked");
    const loginIdMsg = document.getElementById("loginId-msg");
    const checkIdBtn = document.getElementById("check-id");

    const passwordInput = document.getElementById("password");
    const passwordFormatMsg = document.getElementById("password-format-msg");
    const passwordConfirmInput = document.getElementById("passwordConfirm");
    const passwordMsg = document.getElementById("password-msg");

    const phoneInput = document.getElementById("phone");
    const phoneMsg = document.getElementById("phone-msg");

    const emailInput = document.getElementById("email");
    const emailGroup = document.getElementById("email-group");
    const emailCodeGroup = document.getElementById("email-code-group");
    const emailCodeInput = document.getElementById("emailCode");
    const emailVerifiedInput = document.getElementById("emailVerified");
    const emailMsg = document.getElementById("email-msg");
    const sendEmailCodeBtn = document.getElementById("send-email-code");
    const verifyEmailCodeBtn = document.getElementById("verify-email-code");

    const joinForm = document.querySelector(".join-form");
    const joinSubmitBtn = document.getElementById("join-submit-btn");

    function setMsg(el, text, isError) {
        el.textContent = text;
        el.classList.toggle("error-msg", !!isError);
        el.classList.toggle("success-msg", !isError && !!text);
    }

    function csrfHeaders() {
        const csrfInput = joinForm.querySelector('input[name="_csrf"]');
        return csrfInput ? { "X-CSRF-TOKEN": csrfInput.value } : {};
    }

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
        if (!name) {
            setMsg(nameMsg, "", false);
        } else if (!NAME_REGEX.test(name)) {
            setMsg(nameMsg, NAME_ERROR_MESSAGE, true);
        } else {
            setMsg(nameMsg, "", false);
        }
    });

    // 아이디 중복 체크
    checkIdBtn.addEventListener("click", function () {
        const loginId = loginIdInput.value.trim();
        if (!loginId) {
            alert("아이디를 입력해주세요.");
            return;
        }

        fetch(`/members/exists/loginId?loginId=${encodeURIComponent(loginId)}`)
            .then(function (response) {
                return response.json();
            })
            .then(function (duplicated) {
                const isDuplicated = duplicated === true;
                setMsg(loginIdMsg, isDuplicated ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.", isDuplicated);
                loginIdGroup.classList.toggle("checked", !isDuplicated);
                isLoginIdChecked = !isDuplicated;
                loginIdCheckedInput.value = isLoginIdChecked ? "true" : "false";
            });
    });

    loginIdInput.addEventListener("input", function () {
        isLoginIdChecked = false;
        loginIdCheckedInput.value = "false";
        loginIdGroup.classList.remove("checked");

        const loginId = loginIdInput.value.trim();
        if (!loginId) {
            setMsg(loginIdMsg, "", false);
        } else {
            setMsg(loginIdMsg, "아이디 중복 체크를 해주세요.", true);
        }
    });

    // 비밀번호 형식 체크
    passwordInput.addEventListener("input", function () {
        const pw = passwordInput.value;
        if (!pw) {
            setMsg(passwordFormatMsg, "", false);
        } else if (!PASSWORD_REGEX.test(pw)) {
            setMsg(passwordFormatMsg, "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.", true);
        } else {
            setMsg(passwordFormatMsg, "사용 가능한 비밀번호입니다.", false);
        }
        checkPasswordMatch();
    });

    // 비밀번호 확인 일치 여부
    passwordConfirmInput.addEventListener("input", checkPasswordMatch);

    function checkPasswordMatch() {
        const pw = passwordInput.value;
        const pwConfirm = passwordConfirmInput.value;

        if (pwConfirm && pw !== pwConfirm) {
            setMsg(passwordMsg, "비밀번호가 일치하지 않습니다.", true);
        } else if (pwConfirm) {
            setMsg(passwordMsg, "비밀번호가 일치합니다.", false);
        } else {
            setMsg(passwordMsg, "", false);
        }
    }

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

        const len = phoneInput.value.length;
        phoneInput.setSelectionRange(len, len);

        if (phoneTailDigits.length === 8) {
            setMsg(phoneMsg, "", false);
        } else if (phoneTailDigits.length > 0) {
            setMsg(phoneMsg, "전화번호 8자리를 모두 입력해주세요.", true);
        } else {
            setMsg(phoneMsg, "", false);
        }
    }

    renderPhone();

    phoneInput.addEventListener("focus", function () {
        const len = phoneInput.value.length;
        phoneInput.setSelectionRange(len, len);
    });

    phoneInput.addEventListener("click", function () {
        const len = phoneInput.value.length;
        phoneInput.setSelectionRange(len, len);
    });

    phoneInput.addEventListener("keydown", function (e) {
        if (e.ctrlKey || e.metaKey || e.altKey) {
            return;
        }

        if (e.key === "Backspace" || e.key === "Delete") {
            e.preventDefault();
            phoneTailDigits = phoneTailDigits.slice(0, -1);
            renderPhone();
            return;
        }

        if (/^[0-9]$/.test(e.key)) {
            e.preventDefault();
            if (phoneTailDigits.length < 8) {
                phoneTailDigits += e.key;
                renderPhone();
            }
            return;
        }

        if (["ArrowLeft", "ArrowRight", "Tab", "Home", "End", "Shift"].includes(e.key)) {
            return;
        }

        e.preventDefault();
    });

    phoneInput.addEventListener("paste", function (e) {
        e.preventDefault();
        const pasted = (e.clipboardData || window.clipboardData).getData("text");
        const digits = pasted.replace(/[^0-9]/g, "");
        phoneTailDigits = (phoneTailDigits + digits).slice(0, 8);
        renderPhone();
    });

    function resetEmailVerification() {
        isEmailVerified = false;
        emailVerifiedInput.value = "false";
        emailCodeGroup.classList.remove("checked");
        emailCodeInput.value = "";
        emailCodeInput.disabled = true;
        verifyEmailCodeBtn.disabled = true;
        joinSubmitBtn.disabled = true;
    }

    // 이메일 형식 체크 + 인증코드 발송 (이메일 중복 체크는 서버에서 발송 시 함께 처리)
    sendEmailCodeBtn.addEventListener("click", function () {
        const email = emailInput.value.trim();

        if (!email) {
            alert("이메일을 입력해주세요.");
            return;
        }

        if (!EMAIL_REGEX.test(email)) {
            setMsg(emailMsg, "이메일 형식이 올바르지 않습니다.", true);
            return;
        }

        fetch("/members/email/send-code", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded", ...csrfHeaders() },
            body: `email=${encodeURIComponent(email)}`
        })
            .then(function (response) {
                return response.text().then(function (text) {
                    return { ok: response.ok, text: text };
                });
            })
            .then(function (result) {
                setMsg(emailMsg, result.text, !result.ok);
                if (result.ok) {
                    emailCodeInput.disabled = false;
                    verifyEmailCodeBtn.disabled = false;
                }
            });
    });

    // 인증코드 확인
    verifyEmailCodeBtn.addEventListener("click", function () {
        const email = emailInput.value.trim();
        const code = emailCodeInput.value.trim();

        if (!code) {
            alert("인증코드를 입력해주세요.");
            return;
        }

        fetch("/members/email/verify-code", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded", ...csrfHeaders() },
            body: `email=${encodeURIComponent(email)}&code=${encodeURIComponent(code)}`
        })
            .then(function (response) {
                return response.text().then(function (text) {
                    return { ok: response.ok, text: text };
                });
            })
            .then(function (result) {
                setMsg(emailMsg, result.text, !result.ok);
                isEmailVerified = result.ok;
                emailVerifiedInput.value = isEmailVerified ? "true" : "false";
                emailCodeGroup.classList.toggle("checked", isEmailVerified);
                joinSubmitBtn.disabled = !isEmailVerified;
            });
    });

    emailInput.addEventListener("input", function () {
        resetEmailVerification();

        const email = emailInput.value.trim();
        if (!email) {
            setMsg(emailMsg, "", false);
        } else if (!EMAIL_REGEX.test(email)) {
            setMsg(emailMsg, "이메일 형식이 올바르지 않습니다.", true);
        } else {
            setMsg(emailMsg, "인증코드 발송을 눌러주세요.", true);
        }
    });

    joinForm.addEventListener("submit", function (e) {
        const name = nameInput.value.trim();
        const loginId = loginIdInput.value.trim();
        const pw = passwordInput.value;
        const pwConfirm = passwordConfirmInput.value;
        const phone = phoneInput.value.trim();
        const email = emailInput.value.trim();

        if (!name || !loginId || !pw || !pwConfirm || !phone || !email) {
            e.preventDefault();
            alert("모든 항목을 입력해주세요.");
            return;
        }

        let hasError = false;

        if (!NAME_REGEX.test(name)) {
            setMsg(nameMsg, NAME_ERROR_MESSAGE, true);
            hasError = true;
        }

        if (!PASSWORD_REGEX.test(pw)) {
            setMsg(passwordFormatMsg, "비밀번호는 8자 이상이며 영문, 숫자, 특수문자를 모두 포함해야 합니다.", true);
            hasError = true;
        }

        if (pw !== pwConfirm) {
            setMsg(passwordMsg, "비밀번호가 일치하지 않습니다.", true);
            hasError = true;
        }

        if (!EMAIL_REGEX.test(email)) {
            setMsg(emailMsg, "이메일 형식이 올바르지 않습니다.", true);
            hasError = true;
        }

        if (!PHONE_REGEX.test(phone)) {
            setMsg(phoneMsg, "전화번호를 010-XXXX-XXXX 형식으로 모두 입력해주세요.", true);
            hasError = true;
        }

        if (!isLoginIdChecked) {
            setMsg(loginIdMsg, "아이디 중복 체크를 완료해주세요.", true);
            hasError = true;
        }

        if (!isEmailVerified) {
            setMsg(emailMsg, "이메일 인증을 완료해주세요.", true);
            hasError = true;
        }

        if (hasError) {
            e.preventDefault();
        }
    });
});
