
document.addEventListener("DOMContentLoaded", () => {
    const bookForm = document.getElementById("bookForm");
    const generateButton = document.getElementById("generateSummaryButton");
    const saveButton = document.getElementById("saveBookButton");
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

    function getBookData() {
        return {
            title: document.getElementById("title").value.trim(),
            author: document.getElementById("author").value.trim(),
            isbn: document.getElementById("isbn").value.trim() || null,
            publisher: document.getElementById("publisher").value.trim() || null,
            publicationYear: document.getElementById("publicationYear").value
                ? Number(document.getElementById("publicationYear").value)
                : null,
            pageCount: document.getElementById("pageCount").value
                ? Number(document.getElementById("pageCount").value)
                : null,
            coverImageUrl: document.getElementById("coverImageUrl").value.trim() || null,
            description: description.value.trim()
        };
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
            showMessage("Kitap açıklaması başarıyla oluşturuldu. İstersen düzenleyebilirsin.", "success");
        } catch (error) {
            showMessage(error.message, "error");
        } finally {
            generateButton.disabled = false;
            generateButton.innerHTML = "<span>✦</span> Açıklama Oluştur";
        }
    });

    bookForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        clearMessage();

        if (!bookForm.reportValidity()) {
            return;
        }

        const bookData = getBookData();

        if (!bookData.description) {
            showMessage("Kitabı kaydetmeden önce açıklama oluşturmalısın.", "error");
            return;
        }

        saveButton.disabled = true;
        saveButton.textContent = "Kitap kaydediliyor...";

        try {
            const result = await window.shareShelfAuth.apiRequest(
                "/api/books",
                bookData
            );

            showMessage("Kitap başarıyla paylaşıldı. Yönlendiriliyorsun...", "success");

            if (!result.id) {
                throw new Error("Kitap kaydedildi ancak kitap ID bilgisi alınamadı.");
            }

            window.location.href = `/books/${result.id}`;
        } catch (error) {
            showMessage(error.message, "error");
            saveButton.disabled = false;
            saveButton.textContent = "Kitabı Paylaş";
        }
    });
});
