
document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("newShareBookForm");
    const generateButton = document.getElementById("generateSummaryButton");
    const saveButton = document.getElementById("saveBookButton");
    const description = document.getElementById("description");
    const formMessage = document.getElementById("formMessage");

    function showMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = `form-message ${type}`;
    }

    generateButton.addEventListener("click", async () => {
        const title = document.getElementById("title").value.trim();
        const author = document.getElementById("author").value.trim();

        if (!title || !author) {
            showMessage("Önce kitap adını ve yazarını doldurmalısın.", "error");
            return;
        }

        generateButton.disabled = true;
        generateButton.textContent = "Açıklama oluşturuluyor...";

        try {
            const result = await window.shareShelfAuth.apiRequest(
                "/api/books/summary-preview",
                { title, author }
            );

            if (!result.description) {
                throw new Error("Gemini açıklama oluşturamadı.");
            }

            description.value = result.description;
            showMessage(
                "Açıklama oluşturuldu. İstersen düzenleyebilirsin.",
                "success"
            );
        } catch (error) {
            showMessage(error.message, "error");
        } finally {
            generateButton.disabled = false;
            generateButton.textContent = "✦ Gemini ile Açıklama Oluştur";
        }
    });

    form.addEventListener("submit", (event) => {
        if (!form.reportValidity()) {
            event.preventDefault();
            return;
        }

        if (!description.value.trim()) {
            event.preventDefault();
            showMessage("Kitabı eklemeden önce açıklama oluşturmalısın.", "error");
            return;
        }

        saveButton.disabled = true;
        saveButton.textContent = "Kitap ekleniyor...";
    });
});
