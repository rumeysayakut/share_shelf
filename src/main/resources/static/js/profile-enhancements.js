
document.addEventListener("DOMContentLoaded", async () => {
    const auth = window.shareShelfAuth;
    const sharedBooksGrid = document.getElementById("sharedBooksGrid");
    const sharedBooksMessage = document.getElementById("sharedBooksMessage");
    const listBooks = document.getElementById("listBooks");

    if (!auth || !sharedBooksGrid || !listBooks) {
        return;
    }

    let lists = [];

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

    function escapeHtml(value) {
        return String(value ?? "").replace(/[&<>"']/g, character => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[character]);
    }

    function showMessage(element, message, type = "error") {
        element.textContent = message;
        element.className = `profile-message ${type}`;
        element.hidden = false;
    }

    function getShareStatus(status) {
        const statuses = {
            ACTIVE: {
                label: "Ödünç Verilebilir",
                className: "available"
            },
            RESERVED: {
                label: "Ödünç Talebi Var",
                className: "reserved"
            },
            UNAVAILABLE: {
                label: "Şu Anda Kullanılamıyor",
                className: "unavailable"
            },
            CLOSED: {
                label: "Paylaşım Kapalı",
                className: "closed"
            }
        };

        return statuses[status] || {
            label: "Durum Bilinmiyor",
            className: "unavailable"
        };
    }

    async function loadSharedBooks() {
        try {
            const sharedBooks = await request("/api/profile/shared-books");
            renderSharedBooks(sharedBooks);
        } catch (error) {
            sharedBooksGrid.innerHTML = `
                <div class="empty-message">
                    Paylaştığın kitaplar yüklenemedi.
                </div>
            `;
            showMessage(sharedBooksMessage, error.message);
        }
    }

    function renderSharedBooks(sharedBooks) {
        if (!sharedBooks || sharedBooks.length === 0) {
            sharedBooksGrid.innerHTML = `
                <div class="empty-message shared-books-empty">
                    <span>📚</span>
                    <h3>Henüz kitap paylaşmamışsın.</h3>
                    <p>İlk kitabını ShareShelf'e ekleyerek toplulukla paylaşabilirsin.</p>
                    <a href="/shares/new-book">Kitap Paylaş</a>
                </div>
            `;
            return;
        }

        sharedBooksGrid.innerHTML = sharedBooks.map(share => {
            const status = getShareStatus(share.status);
            const imageUrl = share.bookCoverImageUrl || "";
            const safeImageUrl = /^(https?:\/\/|\/)/i.test(imageUrl)
                ? escapeHtml(imageUrl)
                : "";

            return `
                <article class="shared-book-card">
                    <div class="shared-book-cover">
                        ${safeImageUrl
                            ? `<img src="${safeImageUrl}" alt="${escapeHtml(share.bookTitle)}" onerror="this.style.display='none'">`
                            : `<span>📖</span>`}
                    </div>

                    <div class="shared-book-information">
                        <span class="shared-book-status ${status.className}">
                            ${status.label}
                        </span>
                        <h3>${escapeHtml(share.bookTitle)}</h3>
                        <p>${escapeHtml(share.bookAuthor)}</p>
                        <span class="shared-book-date">
                            Paylaşım tarihi:
                            ${share.createdAt
                                ? new Date(share.createdAt).toLocaleDateString("tr-TR")
                                : "Bilinmiyor"}
                        </span>
                    </div>

                    <div class="shared-book-actions">
                        <a href="/books/${share.bookId}/edit"
                           class="shared-book-edit">
                            Kitabı Düzenle
                        </a>
                        <button type="button"
                                class="shared-book-delete"
                                data-book-id="${share.bookId}"
                                data-book-title="${escapeHtml(share.bookTitle)}">
                            Kitabı Sil
                        </button>
                    </div>
                </article>
            `;
        }).join("");

        sharedBooksGrid.querySelectorAll(".shared-book-delete").forEach(button => {
            button.addEventListener("click", async () => {
                const bookId = Number(button.dataset.bookId);
                const bookTitle = button.dataset.bookTitle;

                if (!confirm(
                    `"${bookTitle}" kitabını ShareShelf'ten silmek istediğine emin misin? Bu işlem kitabın paylaşımını kapatır. Başka kullanıcıların kişisel listeleri korunur.`
                )) {
                    return;
                }

                button.disabled = true;

                try {
                    await request(
                        `/api/profile/shared-books/${bookId}`,
                        "DELETE"
                    );

                    await loadSharedBooks();

                    showMessage(
                        sharedBooksMessage,
                        "Kitap paylaşımından kaldırıldı.",
                        "success"
                    );
                } catch (error) {
                    showMessage(sharedBooksMessage, error.message);
                    button.disabled = false;
                }
            });
        });
    }

    async function loadListsForActions() {
        lists = await request("/api/reading-lists");
    }

    function addListActionsToCards() {
        const activeListCard = document.querySelector(".list-card.active");
        const currentListId = activeListCard
            ? Number(activeListCard.dataset.listId)
            : null;

        if (!currentListId) {
            return;
        }

        listBooks.querySelectorAll(".list-book-card").forEach(card => {
            if (card.dataset.listActionsAdded === "true") {
                return;
            }

            const removeButton = card.querySelector(".remove-book-button");

            if (!removeButton) {
                return;
            }

            const bookId = Number(removeButton.dataset.bookId);
            const otherLists = lists.filter(list => list.id !== currentListId);

            const actions = document.createElement("div");
            actions.className = "profile-list-actions";
            actions.dataset.bookId = String(bookId);

            if (otherLists.length === 0) {
                actions.innerHTML = `
                    <p>Kitabı başka listeye eklemek için önce yeni bir liste oluşturmalısın.</p>
                `;
            } else {
                actions.innerHTML = `
                    <select class="profile-target-list">
                        <option value="">Hedef liste seç</option>
                        ${otherLists.map(list => `
                            <option value="${list.id}">${escapeHtml(list.name)}</option>
                        `).join("")}
                    </select>
                    <div class="profile-list-action-buttons">
                        <button type="button" class="profile-copy-book">
                            Diğer Listeye Ekle
                        </button>
                        <button type="button" class="profile-move-book">
                            Diğer Listeye Taşı
                        </button>
                    </div>
                `;
            }

            card.appendChild(actions);
            card.dataset.listActionsAdded = "true";

            const select = actions.querySelector(".profile-target-list");
            const copyButton = actions.querySelector(".profile-copy-book");
            const moveButton = actions.querySelector(".profile-move-book");

            if (!select || !copyButton || !moveButton) {
                return;
            }

            copyButton.addEventListener("click", async () => {
                const targetListId = Number(select.value);

                if (!targetListId) {
                    showMessage(
                        document.getElementById("profileMessage"),
                        "Önce hedef liste seçmelisin."
                    );
                    return;
                }

                copyButton.disabled = true;

                try {
                    await request(
                        `/api/reading-lists/${targetListId}/books`,
                        "POST",
                        { bookId }
                    );

                    showMessage(
                        document.getElementById("profileMessage"),
                        "Kitap seçtiğin listeye de eklendi. Mevcut listende kalmaya devam ediyor.",
                        "success"
                    );
                } catch (error) {
                    showMessage(
                        document.getElementById("profileMessage"),
                        error.message
                    );
                } finally {
                    copyButton.disabled = false;
                }
            });

            moveButton.addEventListener("click", async () => {
                const targetListId = Number(select.value);

                if (!targetListId) {
                    showMessage(
                        document.getElementById("profileMessage"),
                        "Önce hedef liste seçmelisin."
                    );
                    return;
                }

                if (!confirm("Kitap mevcut listeden çıkarılıp seçtiğin listeye taşınacak. Devam edilsin mi?")) {
                    return;
                }

                moveButton.disabled = true;

                try {
                    await request(
                        `/api/reading-lists/${currentListId}/books/${bookId}/move/${targetListId}`,
                        "POST"
                    );

                    card.remove();

                    const remainingBooks =
                        listBooks.querySelectorAll(".list-book-card").length;

                    document.getElementById("listBookCount").textContent =
                        `${remainingBooks} kitap`;

                    showMessage(
                        document.getElementById("profileMessage"),
                        "Kitap seçtiğin listeye taşındı.",
                        "success"
                    );
                } catch (error) {
                    showMessage(
                        document.getElementById("profileMessage"),
                        error.message
                    );
                    moveButton.disabled = false;
                }
            });
        });
    }

    try {
        await auth.refreshAccessToken();
        await Promise.all([
            loadSharedBooks(),
            loadListsForActions()
        ]);

        const observer = new MutationObserver(() => {
            addListActionsToCards();
        });

        observer.observe(listBooks, {
            childList: true,
            subtree: true
        });

        addListActionsToCards();

        document.getElementById("listGrid").addEventListener("click", () => {
            setTimeout(addListActionsToCards, 100);
        });
    } catch (error) {
        showMessage(sharedBooksMessage, error.message);
    }
});
