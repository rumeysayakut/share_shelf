document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("shareSearch");
    const shareCards = document.querySelectorAll(".share-card");
    const searchEmpty = document.getElementById("shareSearchEmpty");
    const sharesGrid = document.getElementById("sharesGrid");

    if (!searchInput) {
        return;
    }

    searchInput.addEventListener("input", () => {
        const searchValue = searchInput.value
            .toLocaleLowerCase("tr-TR")
            .trim();

        let visibleCount = 0;

        shareCards.forEach((card) => {
            const searchableText = (card.dataset.search || "")
                .toLocaleLowerCase("tr-TR");

            const matches = searchableText.includes(searchValue);

            card.hidden = !matches;

            if (matches) {
                visibleCount++;
            }
        });

        if (searchEmpty) {
            searchEmpty.hidden = visibleCount !== 0 || shareCards.length === 0;
        }

        if (sharesGrid) {
            sharesGrid.hidden = visibleCount === 0;
        }
    });
});