
document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("bookSearch");
    const bookCards = document.querySelectorAll(".book-card");
    const noSearchResult = document.getElementById("noSearchResult");

    if (!searchInput) {
        return;
    }

    searchInput.addEventListener("input", () => {
        const searchValue = searchInput.value
            .toLocaleLowerCase("tr-TR")
            .trim();

        let visibleCount = 0;

        bookCards.forEach((card) => {
            const title = card.querySelector("h3").textContent.toLocaleLowerCase("tr-TR");
            const author = card.querySelector(".book-author").textContent.toLocaleLowerCase("tr-TR");

            const matches = title.includes(searchValue) || author.includes(searchValue);

            card.hidden = !matches;

            if (matches) {
                visibleCount++;
            }
        });

        if (noSearchResult) {
            noSearchResult.hidden = visibleCount !== 0 || bookCards.length === 0;
        }
    });
});
