
document.addEventListener("DOMContentLoaded", () => {
    const generateButton = document.getElementById("generateSummaryButton");
    const description = document.getElementById("description");
    const formMessage = document.getElementById("formMessage");

    function showMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = `form-message ${type}`;
    }

    function clearMessage() {
        formMessage.textContent = "";
        formMessage.className = "form-message";
    }

    generateButton.addEventListener("click", async () => {
        clearMessage();

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
                "Kitap açıklaması oluşturuldu. İstersen düzenleyebilirsin.",
                "success"
            );
        } catch (error) {
            showMessage(error.message, "error");
        } finally {
            generateButton.disabled = false;
            generateButton.innerHTML = "<span>✦</span> Açıklama Oluştur";
        }
    });
});
