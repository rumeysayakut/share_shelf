
document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("newShareBookForm");
    const generateButton = document.getElementById("generateSummaryButton");
    const saveButton = document.getElementById("saveBookButton");
    const description = document.getElementById("description");
    const formMessage = document.getElementById("formMessage");
    const imageInput = document.getElementById("coverImage");
    const imagePreview = document.getElementById("imagePreview");
    const imagePreviewContainer = document.getElementById("imagePreviewContainer");
    const removeImageButton = document.getElementById("removeImageButton");
    const imageError = document.getElementById("imageError");

    let previewUrl = null;

    function showMessage(message, type) {
        formMessage.textContent = message;
        formMessage.className = `form-message ${type}`;
    }

    function clearImagePreview() {
        imageInput.value = "";
        imagePreview.src = "";
        imagePreviewContainer.hidden = true;
        imageError.textContent = "";

        if (previewUrl) {
            URL.revokeObjectURL(previewUrl);
            previewUrl = null;
        }
    }

    imageInput.addEventListener("change", () => {
        imageError.textContent = "";

        const file = imageInput.files[0];

        if (!file) {
            clearImagePreview();
            return;
        }

        const allowedTypes = ["image/jpeg", "image/png", "image/webp"];

        if (!allowedTypes.includes(file.type)) {
            clearImagePreview();
            imageError.textContent = "Yalnızca JPG, PNG veya WEBP dosyası seçebilirsin.";
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            clearImagePreview();
            imageError.textContent = "Fotoğraf en fazla 5 MB olabilir.";
            return;
        }

        if (previewUrl) {
            URL.revokeObjectURL(previewUrl);
        }

        previewUrl = URL.createObjectURL(file);
        imagePreview.src = previewUrl;
        imagePreviewContainer.hidden = false;
    });

    removeImageButton.addEventListener("click", clearImagePreview);

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

        const file = imageInput.files[0];

        if (file) {
            const allowedTypes = ["image/jpeg", "image/png", "image/webp"];

            if (!allowedTypes.includes(file.type) || file.size > 5 * 1024 * 1024) {
                event.preventDefault();
                imageError.textContent = "Fotoğraf türünü ve 5 MB sınırını kontrol et.";
                return;
            }
        }

        saveButton.disabled = true;
        saveButton.textContent = "Kitap ekleniyor...";
    });
});
