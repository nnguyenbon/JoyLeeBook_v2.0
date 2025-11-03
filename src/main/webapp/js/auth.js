function togglePassword(iconElement, id) {
    const inputElement = document.getElementById(id);
    iconElement.classList.toggle('fa-eye-slash');
    iconElement.classList.toggle('fa-eye');

    if (inputElement.type === 'password') {
        inputElement.type = 'text';
    } else {
        inputElement.type = 'password';
    }
}

let debounceTimer;

function debounce(func, delay) {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(func, delay);
}


document.getElementById("username").addEventListener("input", function () {
    const username = this.value.trim();
    const errorElement = document.getElementById("username-error");

    debounce(() => {

        fetch("/register?type=username&value=" + encodeURIComponent(username))
            .then(res => res.json())
            .then(data => {
                if (data.valid) {
                    errorElement.textContent = "";
                } else {
                    errorElement.textContent = data.message;
                }
                validateForm();
            })
            .catch(() => {
                errorElement.textContent = "";
                validateForm();
            });
    }, 400);
});


document.getElementById("fullName").addEventListener("input", function () {
    const username = this.value.trim();
    const errorElement = document.getElementById("fullname-error");

    debounce(() => {

        fetch("/register?type=fullname&value=" + encodeURIComponent(username))
            .then(res => res.json())
            .then(data => {
                if (data.valid) {
                    errorElement.textContent = "";
                } else {
                    errorElement.textContent = data.message;
                }
                validateForm();
            })
            .catch(() => {
                errorElement.textContent = "";
                validateForm();
            });
    }, 400);
});

document.getElementById("email").addEventListener("input", function () {
    const email = this.value.trim();
    const errorElement = document.getElementById("email-error");

    debounce(() => {
        fetch("/register?type=email&value=" + encodeURIComponent(email))
            .then(res => res.json())
            .then(data => {
                if (data.valid) {
                    errorElement.textContent = "";
                } else {
                    errorElement.textContent = data.message;
                }
                validateForm();
            })
            .catch(() => {
                errorElement.textContent = "";
                validateForm();
            });
    }, 400);
});

document.getElementById("password").addEventListener("input", function () {
    const email = this.value.trim();
    const errorElement = document.getElementById("password-error");

    debounce(() => {
        fetch("/register?type=password&value=" + encodeURIComponent(email))
            .then(res => res.json())
            .then(data => {
                if (data.valid) {
                    errorElement.textContent = "";
                } else {
                    errorElement.textContent = data.message;
                }
                validateForm();
            })
            .catch(() => {
                errorElement.textContent = "";
                validateForm();
            });
    }, 400);
});

function validateForm() {
    const username = document.getElementById("username").value.trim();
    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();

    const usernameError = document.getElementById("username-error").textContent.trim();
    const fullnameError = document.getElementById("fullname-error").textContent.trim();
    const emailError = document.getElementById("email-error").textContent.trim();
    const passwordError = document.getElementById("password-error").textContent.trim();

    const submitBtn = document.getElementById("submit-btn");

    const isValid =
        username &&
        fullName &&
        email &&
        password &&
        confirmPassword &&
        password === confirmPassword &&
        !usernameError &&
        !fullnameError &&
        !emailError &&
        !passwordError;
    submitBtn.disabled = !isValid;
}

document.getElementById("confirmPassword").addEventListener("input", validateForm);
document.addEventListener("DOMContentLoaded", validateForm);
