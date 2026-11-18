
let accessToken = null;
let refreshPromise = null;

async function getCsrfToken() {
    const response = await fetch("/api/csrf", {
        credentials: "same-origin"
    });

    if (!response.ok) {
        throw new Error("Güvenlik anahtarı alınamadı.");
    }

    const data = await response.json();
    return data.token;
}

async function readResponse(response) {
    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
        throw new Error(
            data.message ||
            data.detail ||
            data.title ||
            "İşlem başarısız oldu."
        );
    }

    return data;
}

async function authRequest(url, requestData) {
    const csrfToken = await getCsrfToken();

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken
        },
        credentials: "same-origin",
        body: JSON.stringify(requestData)
    });

    const data = await readResponse(response);

    if (data.accessToken) {
        accessToken = data.accessToken;
    }

    return data;
}

async function apiRequest(url, requestData) {
    if (!accessToken) {
        await refreshAccessToken();
    }

    const csrfToken = await getCsrfToken();

    const response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
            "X-XSRF-TOKEN": csrfToken
        },
        credentials: "same-origin",
        body: JSON.stringify(requestData)
    });

    return readResponse(response);
}

async function login(email, password) {
    return authRequest("/api/auth/login", {
        email,
        password
    });
}

async function register(firstName, lastName, email, password) {
    return authRequest("/api/auth/register", {
        firstName,
        lastName,
        email,
        password
    });
}

async function refreshAccessToken() {
    if (refreshPromise) {
        return refreshPromise;
    }

    refreshPromise = (async () => {
        const csrfToken = await getCsrfToken();

        const response = await fetch("/api/auth/refresh", {
            method: "POST",
            headers: {
                "X-XSRF-TOKEN": csrfToken
            },
            credentials: "same-origin"
        });

        const data = await readResponse(response);
        accessToken = data.accessToken;

        return accessToken;
    })();

    try {
        return await refreshPromise;
    } catch (error) {
        accessToken = null;
        throw error;
    } finally {
        refreshPromise = null;
    }
}

async function logout() {
    const csrfToken = await getCsrfToken();

    const response = await fetch("/api/auth/logout", {
        method: "POST",
        headers: {
            "X-XSRF-TOKEN": csrfToken
        },
        credentials: "same-origin"
    });

    await readResponse(response);
    accessToken = null;
}

function getAccessToken() {
    return accessToken;
}

function clearAccessToken() {
    accessToken = null;
}

window.shareShelfAuth = {
    login,
    register,
    logout,
    refreshAccessToken,
    getAccessToken,
    clearAccessToken,
    apiRequest,
    getCsrfToken
};

document.addEventListener("DOMContentLoaded", async () => {
    try {
        await refreshAccessToken();
    } catch (error) {
        accessToken = null;
    }

    const loginForm = document.getElementById("loginForm");

    if (loginForm) {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const button = document.getElementById("loginButton");
            const errorMessage = document.getElementById("errorMessage");

            button.disabled = true;
            button.textContent = "Giriş yapılıyor...";
            errorMessage.textContent = "";

            try {
                await login(
                    document.getElementById("email").value.trim(),
                    document.getElementById("password").value
                );

                window.location.href = "/books";
            } catch (error) {
                errorMessage.textContent = error.message;
            } finally {
                button.disabled = false;
                button.textContent = "Giriş Yap";
            }
        });
    }

    const registerForm = document.getElementById("registerForm");

    if (registerForm) {
        registerForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const button = document.getElementById("registerButton");
            const errorMessage = document.getElementById("errorMessage");

            button.disabled = true;
            button.textContent = "Hesap oluşturuluyor...";
            errorMessage.textContent = "";

            try {
                await register(
                    document.getElementById("firstName").value.trim(),
                    document.getElementById("lastName").value.trim(),
                    document.getElementById("email").value.trim(),
                    document.getElementById("password").value
                );

                window.location.href = "/books";
            } catch (error) {
                errorMessage.textContent = error.message;
            } finally {
                button.disabled = false;
                button.textContent = "Hesap Oluştur";
            }
        });
    }

    const logoutButton = document.getElementById("logoutButton");

    if (logoutButton) {
        logoutButton.addEventListener("click", async (event) => {
            event.preventDefault();

            try {
                await logout();
                window.location.href = "/books";
            } catch (error) {
                alert(error.message);
            }
        });
    }
});




(function attachCsrfToForms() {
    function getCookie(name) {
        const match = document.cookie
            .split("; ")
            .find(row => row.startsWith(name + "="));
        return match ? decodeURIComponent(match.split("=")[1]) : null;
    }

    function injectCsrfTokens() {
        const csrfToken = getCookie("XSRF-TOKEN");
        if (!csrfToken) return;

        document.querySelectorAll('form[method="post"], form[method="POST"]').forEach(form => {
            let input = form.querySelector('input[name="_csrf"]');
            if (!input) {
                input = document.createElement("input");
                input.type = "hidden";
                input.name = "_csrf";
                form.appendChild(input);
            }
            input.value = csrfToken;
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", injectCsrfTokens);
    } else {
        injectCsrfTokens();
    }


    document.addEventListener("submit", (event) => {
        const form = event.target;
        if (form && form.method && form.method.toUpperCase() === "POST") {
            const csrfToken = getCookie("XSRF-TOKEN");
            if (csrfToken) {
                let input = form.querySelector('input[name="_csrf"]');
                if (!input) {
                    input = document.createElement("input");
                    input.type = "hidden";
                    input.name = "_csrf";
                    form.appendChild(input);
                }
                input.value = csrfToken;
            }
        }
    }, true);
})();