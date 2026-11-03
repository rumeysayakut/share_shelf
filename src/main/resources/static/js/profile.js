document.addEventListener("DOMContentLoaded", async () => {
    const auth = window.shareShelfAuth;

    const listGrid = document.getElementById("listGrid");
    const listForm = document.getElementById("listForm");
    const listName = document.getElementById("listName");
    const listDescription = document.getElementById("listDescription");
    const listPublic = document.getElementById("listPublic");
    const listMessage = document.getElementById("listMessage");
    const selectedListSection = document.getElementById("selectedListSection");
    const selectedListName = document.getElementById("selectedListName");
    const selectedListDescription = document.getElementById("selectedListDescription");
    const selectedListVisibility = document.getElementById("selectedListVisibility");
    const listBooks = document.getElementById("listBooks");
    const listBookCount = document.getElementById("listBookCount");
    const bookSearch = document.getElementById("bookSearch");
    const availableBooks = document.getElementById("availableBooks");
    const addBookModal = document.getElementById("addBookModal");
    const newBookForm = document.getElementById("newBookForm");
    const newBookMessage = document.getElementById("newBookMessage");

    let lists = [];
    let books = [];
    let library = [];
    let selectedListId = null;
    let editingListId = null;
    let currentListBooks = [];

    async function request(url, method = "GET", body = null) {
        const token = auth.getAccessToken();

        const options = {
            method,
            credentials: "same-origin",
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        if (body !== null) {
            options.headers["Content-Type"] = "application/json";
            options.body = JSON.stringify(body);
        }

        const response = await fetch(url, options);

        if (response.status === 204) {
            if (!response.ok) {
                throw new Error("İşlem sırasında bir hata oluştu.");
            }
            return null;
        }

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            throw new Error(
                data?.detail ||
                data?.message ||
                data?.title ||
                "İşlem sırasında bir hata oluştu."
            );
        }

        return data;
    }

    function showMessage(element, message, type = "error") {
        element.textContent = message;
        element.className = `profile-message ${type}`;
        element.hidden = false;
    }

    function hideMessage(element) {
        element.hidden = true;
        element.textContent = "";
    }

    function escapeHtml(value) {
        return String(value ?? "").replace(/[&<>"']/g, character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[character]);
    }

    function getSelectedList() {
        return lists.find(list => list.id === selectedListId);
    }

    async function loadLists() {
        lists = await request("/api/reading-lists");
        renderLists();
    }

    async function loadBooks() {
        books = await request("/api/books");
    }

    async function loadLibrary() {
        library = await request("/api/user-library");
    }

    function renderLists() {
        if (lists.length === 0) {
            listGrid.innerHTML = `
                <div class="empty-message">
                    <span>📚</span>
                    <h3>Henüz bir kitap listen yok.</h3>
                    <p>İlk listeni oluştur ve kitaplarını kendi koleksiyonunda bir araya getir.</p>
                </div>
            `;
            return;
        }

        listGrid.innerHTML = lists.map((list, index) => `
            <article class="list-card ${selectedListId === list.id ? "active" : ""}"
                     data-list-id="${list.id}">
                <div class="list-card-top">
                    <span class="list-card-icon">${["📚", "🌿", "✨", "☀️"][index % 4]}</span>
                    <span class="list-card-visibility">
                        ${list.isPublic ? "🌍 Herkese açık" : "🔒 Özel"}
                    </span>
                </div>
                <h3>${escapeHtml(list.name)}</h3>
                <p>${escapeHtml(list.description || "Bu liste için henüz açıklama eklenmemiş.")}</p>
                <div class="list-card-footer">
                    <span>Listeyi görüntüle</span>
                    <span>→</span>
                </div>
            </article>
        `).join("");

        listGrid.querySelectorAll(".list-card").forEach(card => {
            card.addEventListener("click", () => {
                openList(Number(card.dataset.listId));
            });
        });
    }

    async function openList(listId) {
        selectedListId = listId;

        const list = getSelectedList();

        if (!list) {
            return;
        }

        selectedListName.textContent = list.name;
        selectedListDescription.textContent = list.description || "Bu liste için henüz açıklama eklenmemiş.";
        updateVisibilityBadge(list);

        selectedListSection.hidden = false;

        renderLists();

        try {
            await loadListBooks();
            selectedListSection.scrollIntoView({
                behavior: "smooth",
                block: "start"
            });
        } catch (error) {
            showMessage(document.getElementById("profileMessage"), error.message);
        }
    }

    function updateVisibilityBadge(list) {
        selectedListVisibility.textContent = list.isPublic
            ? "🌍 Herkese açık"
            : "🔒 Yalnızca sen görebilirsin";

        selectedListVisibility.className = list.isPublic
            ? "list-visibility-badge public"
            : "list-visibility-badge private";
    }

    async function loadListBooks() {
        currentListBooks = await request(`/api/reading-lists/${selectedListId}/books`);
        await loadLibrary();
        renderListBooks();
        renderAvailableBooks();
    }

    function getReadingStatus(bookId) {
        const libraryItem = library.find(item => item.bookId === bookId);
        return libraryItem?.readingStatus || "WANT_TO_READ";
    }

    function getStatusLabel(status) {
        const labels = {
            WANT_TO_READ: "Okumak İstiyorum",
            READING: "Okuyorum",
            READ: "Okudum"
        };

        return labels[status] || "Okumak İstiyorum";
    }

    function renderListBooks() {
        listBookCount.textContent = `${currentListBooks.length} kitap`;

        if (currentListBooks.length === 0) {
            listBooks.innerHTML = `
                <div class="empty-message">
                    <span>🌱</span>
                    <h3>Bu liste henüz boş.</h3>
                    <p>Kitap Ekle butonuna tıklayarak ilk kitabını listene ekleyebilirsin.</p>
                </div>
            `;
            return;
        }

        listBooks.innerHTML = currentListBooks.map(item => {
            const status = getReadingStatus(item.bookId);

            return `
                <article class="list-book-card">
                    <div class="book-card-symbol">📖</div>
                    <div class="list-book-information">
                        <h4>${escapeHtml(item.bookTitle)}</h4>
                        <p>${escapeHtml(item.bookAuthor)}</p>
                        <label for="status-${item.bookId}">Okuma durumum</label>
                        <select class="reading-status-select" id="status-${item.bookId}"
                                data-book-id="${item.bookId}">
                            <option value="WANT_TO_READ" ${status === "WANT_TO_READ" ? "selected" : ""}>
                                Okumak İstiyorum
                            </option>
                            <option value="READING" ${status === "READING" ? "selected" : ""}>
                                Okuyorum
                            </option>
                            <option value="READ" ${status === "READ" ? "selected" : ""}>
                                Okudum
                            </option>
                        </select>
                    </div>
                    <button class="remove-book-button" type="button"
                            data-book-id="${item.bookId}">
                        Listeden Çıkar
                    </button>
                </article>
            `;
        }).join("");

        listBooks.querySelectorAll(".reading-status-select").forEach(select => {
            select.addEventListener("change", async () => {
                const bookId = Number(select.dataset.bookId);
                const previousStatus = getReadingStatus(bookId);

                select.disabled = true;

                try {
                    await updateLibraryStatus(bookId, select.value);
                    showMessage(
                        document.getElementById("profileMessage"),
                        "Okuma durumun güncellendi.",
                        "success"
                    );
                } catch (error) {
                    select.value = previousStatus;
                    showMessage(document.getElementById("profileMessage"), error.message);
                } finally {
                    select.disabled = false;
                }
            });
        });

        listBooks.querySelectorAll(".remove-book-button").forEach(button => {
            button.addEventListener("click", async () => {
                const bookId = Number(button.dataset.bookId);

                if (!confirm("Bu kitabı listenden çıkarmak istiyor musun? Kitap kaydı silinmeyecek.")) {
                    return;
                }

                button.disabled = true;

                try {
                    await request(
                        `/api/reading-lists/${selectedListId}/books/${bookId}`,
                        "DELETE"
                    );

                    await loadListBooks();
                    showMessage(
                        document.getElementById("profileMessage"),
                        "Kitap listenden çıkarıldı.",
                        "success"
                    );
                } catch (error) {
                    showMessage(document.getElementById("profileMessage"), error.message);
                    button.disabled = false;
                }
            });
        });
    }

    async function updateLibraryStatus(bookId, status) {
        const libraryItem = library.find(item => item.bookId === bookId);

        if (libraryItem) {
            await request(
                `/api/user-library/${bookId}/status?readingStatus=${status}`,
                "PATCH"
            );
        } else {
            await request("/api/user-library", "POST", {
                bookId,
                readingStatus: status
            });
        }

        await loadLibrary();
    }

    async function addBookToSelectedList(bookId) {
        await request(
            `/api/reading-lists/${selectedListId}/books`,
            "POST",
            { bookId }
        );

        await updateLibraryStatus(bookId, "WANT_TO_READ");
        await loadListBooks();
    }

    function renderAvailableBooks() {
        const search = bookSearch.value.toLocaleLowerCase("tr-TR").trim();
        const existingIds = currentListBooks.map(item => item.bookId);

        const filteredBooks = books.filter(book => {
            const searchable = `${book.title} ${book.author}`.toLocaleLowerCase("tr-TR");
            return searchable.includes(search) && !existingIds.includes(book.id);
        });

        if (filteredBooks.length === 0) {
            availableBooks.innerHTML = `
                <div class="empty-message compact-empty">
                    ${search
                        ? "Aramana uygun eklenebilir kitap bulunamadı."
                        : "Eklenebilecek başka kitap bulunamadı."}
                </div>
            `;
            return;
        }

        availableBooks.innerHTML = filteredBooks.map(book => `
            <div class="available-book">
                <div class="available-book-icon">📘</div>
                <div class="available-book-info">
                    <strong>${escapeHtml(book.title)}</strong>
                    <span>${escapeHtml(book.author)}</span>
                </div>
                <button class="add-book-button" type="button" data-book-id="${book.id}">
                    ＋ Ekle
                </button>
            </div>
        `).join("");

        availableBooks.querySelectorAll(".add-book-button").forEach(button => {
            button.addEventListener("click", async () => {
                button.disabled = true;

                try {
                    await addBookToSelectedList(Number(button.dataset.bookId));
                    renderAvailableBooks();
                    showMessage(
                        document.getElementById("profileMessage"),
                        "Kitap listene eklendi.",
                        "success"
                    );
                } catch (error) {
                    showMessage(document.getElementById("profileMessage"), error.message);
                    button.disabled = false;
                }
            });
        });
    }

    function openListForm(list = null) {
        editingListId = list ? list.id : null;
        listForm.reset();

        if (list) {
            listName.value = list.name;
            listDescription.value = list.description || "";
            listPublic.checked = Boolean(list.isPublic);
            document.getElementById("listFormTitle").textContent = "Listeyi Düzenle";
            document.getElementById("saveListButton").textContent = "Değişiklikleri Kaydet";
        } else {
            document.getElementById("listFormTitle").textContent = "Yeni kitap listesi";
            document.getElementById("saveListButton").textContent = "Listeyi Oluştur";
        }

        hideMessage(listMessage);
        listForm.hidden = false;
        listForm.scrollIntoView({ behavior: "smooth", block: "center" });
        listName.focus();
    }

    document.getElementById("showListForm").addEventListener("click", () => {
        openListForm();
    });

    function closeListForm() {
        listForm.hidden = true;
        listForm.reset();
        editingListId = null;
    }

    document.getElementById("cancelListForm").addEventListener("click", closeListForm);
    document.getElementById("cancelListButton").addEventListener("click", closeListForm);

    listForm.addEventListener("submit", async event => {
        event.preventDefault();

        const name = listName.value.trim();

        if (!name) {
            showMessage(listMessage, "Lütfen liste adı gir.");
            return;
        }

        const data = {
            name,
            description: listDescription.value.trim(),
            isPublic: listPublic.checked
        };

        const button = document.getElementById("saveListButton");
        button.disabled = true;

        try {
            if (editingListId) {
                await request(`/api/reading-lists/${editingListId}`, "PUT", data);
            } else {
                await request("/api/reading-lists", "POST", data);
            }

            closeListForm();
            await loadLists();

            if (selectedListId) {
                const updatedList = getSelectedList();

                if (updatedList) {
                    selectedListName.textContent = updatedList.name;
                    selectedListDescription.textContent = updatedList.description || "Bu liste için henüz açıklama eklenmemiş.";
                    updateVisibilityBadge(updatedList);
                }
            }

            showMessage(
                listMessage,
                "Liste bilgileri kaydedildi.",
                "success"
            );
        } catch (error) {
            showMessage(listMessage, error.message);
        } finally {
            button.disabled = false;
        }
    });

    document.getElementById("editListButton").addEventListener("click", () => {
        const list = getSelectedList();

        if (list) {
            openListForm(list);
        }
    });

    document.getElementById("deleteListButton").addEventListener("click", async () => {
        if (!confirm("Bu listeyi silmek istediğine emin misin? Kitap kayıtların silinmeyecek.")) {
            return;
        }

        try {
            await request(`/api/reading-lists/${selectedListId}`, "DELETE");

            selectedListId = null;
            selectedListSection.hidden = true;

            await loadLists();

            showMessage(
                document.getElementById("profileMessage"),
                "Listen silindi.",
                "success"
            );
        } catch (error) {
            showMessage(document.getElementById("profileMessage"), error.message);
        }
    });

    function openAddBookModal() {
        addBookModal.hidden = false;
        bookSearch.value = "";
        showAddBookTab("existing");
        renderAvailableBooks();
        document.body.classList.add("modal-open");
    }

    function closeAddBookModal() {
        addBookModal.hidden = true;
        document.body.classList.remove("modal-open");
    }

    function showAddBookTab(tab) {
        document.querySelectorAll(".add-book-tab").forEach(button => {
            button.classList.toggle("active", button.dataset.tab === tab);
        });

        document.getElementById("existingBookPanel").hidden = tab !== "existing";
        document.getElementById("newBookPanel").hidden = tab !== "new";
    }

    document.getElementById("showAddBook").addEventListener("click", openAddBookModal);
    document.getElementById("closeAddBook").addEventListener("click", closeAddBookModal);

    addBookModal.addEventListener("click", event => {
        if (event.target === addBookModal) {
            closeAddBookModal();
        }
    });

    document.querySelectorAll(".add-book-tab").forEach(button => {
        button.addEventListener("click", () => {
            showAddBookTab(button.dataset.tab);
        });
    });

    bookSearch.addEventListener("input", renderAvailableBooks);

    document.getElementById("generateSummaryButton").addEventListener("click", async () => {
        hideMessage(newBookMessage);

        const title = document.getElementById("newBookTitle").value.trim();
        const author = document.getElementById("newBookAuthor").value.trim();
        const button = document.getElementById("generateSummaryButton");

        if (!title || !author) {
            showMessage(newBookMessage, "Önce kitap adını ve yazarını doldurmalısın.");
            return;
        }

        button.disabled = true;
        button.textContent = "Açıklama oluşturuluyor...";

        try {
            const result = await auth.apiRequest("/api/books/summary-preview", {
                title,
                author
            });

            if (!result.description) {
                throw new Error("Gemini açıklama oluşturamadı.");
            }

            document.getElementById("newBookDescription").value = result.description;

            showMessage(
                newBookMessage,
                "Açıklama oluşturuldu. İstersen kaydetmeden önce düzenleyebilirsin.",
                "success"
            );
        } catch (error) {
            showMessage(newBookMessage, error.message);
        } finally {
            button.disabled = false;
            button.textContent = "✦ Açıklama Oluştur";
        }
    });

    newBookForm.addEventListener("submit", async event => {
        event.preventDefault();
        hideMessage(newBookMessage);

        if (!newBookForm.reportValidity()) {
            return;
        }

        const description = document.getElementById("newBookDescription").value.trim();

        if (!description) {
            showMessage(newBookMessage, "Kitabı kaydetmeden önce Gemini ile açıklama oluşturmalısın.");
            return;
        }

        const bookData = {
            title: document.getElementById("newBookTitle").value.trim(),
            author: document.getElementById("newBookAuthor").value.trim(),
            isbn: document.getElementById("newBookIsbn").value.trim() || null,
            publisher: document.getElementById("newBookPublisher").value.trim() || null,
            publicationYear: document.getElementById("newBookYear").value
                ? Number(document.getElementById("newBookYear").value)
                : null,
            pageCount: document.getElementById("newBookPageCount").value
                ? Number(document.getElementById("newBookPageCount").value)
                : null,
            coverImageUrl: document.getElementById("newBookCover").value.trim() || null,
            description
        };

        const button = document.getElementById("saveNewBookButton");
        button.disabled = true;
        button.textContent = "Kitap oluşturuluyor...";

        try {
            const createdBook = await auth.apiRequest("/api/books", bookData);

            if (!createdBook.id) {
                throw new Error("Kitap oluşturuldu ancak kitap bilgisi alınamadı.");
            }

            await request(
                `/api/reading-lists/${selectedListId}/books`,
                "POST",
                { bookId: createdBook.id }
            );

            await updateLibraryStatus(createdBook.id, "WANT_TO_READ");

            newBookForm.reset();
            await Promise.all([loadBooks(), loadListBooks()]);

            closeAddBookModal();

            showMessage(
                document.getElementById("profileMessage"),
                "Kitap oluşturuldu ve listene eklendi.",
                "success"
            );
        } catch (error) {
            showMessage(newBookMessage, error.message);
        } finally {
            button.disabled = false;
            button.textContent = "Kitabı Oluştur ve Listeye Ekle";
        }
    });

    try {
        await auth.refreshAccessToken();
        await Promise.all([loadLists(), loadBooks(), loadLibrary()]);
        renderLists();
    } catch (error) {
        showMessage(listMessage, error.message);
        listGrid.innerHTML = `
            <div class="empty-message">
                Listeler yüklenemedi. Sayfayı yenileyip tekrar deneyebilirsin.
            </div>
        `;
    }
});