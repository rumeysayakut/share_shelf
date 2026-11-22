
package com.shareshelf.service;

import com.shareshelf.entity.ReadingList;
import com.shareshelf.entity.ReadingListItem;
import com.shareshelf.entity.User;
import com.shareshelf.repository.ReadingListItemRepository;
import com.shareshelf.repository.ReadingListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReadingListMoveService {

    private final ReadingListRepository readingListRepository;
    private final ReadingListItemRepository readingListItemRepository;

    @Transactional
    public void moveBook(
            Long sourceListId,
            Long targetListId,
            Long bookId,
            User currentUser) {

        if (sourceListId.equals(targetListId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Kitap zaten bu listede bulunuyor."
            );
        }

        ReadingList sourceList = findUserList(sourceListId, currentUser);
        ReadingList targetList = findUserList(targetListId, currentUser);

        ReadingListItem sourceItem = readingListItemRepository
                .findByReadingListIdAndBookId(sourceList.getId(), bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Kitap kaynak listede bulunamadı."
                ));

        boolean alreadyInTarget =
                readingListItemRepository.existsByReadingListIdAndBookId(
                        targetList.getId(),
                        bookId
                );

        if (alreadyInTarget) {
            readingListItemRepository.delete(sourceItem);
            return;
        }

        sourceItem.setReadingList(targetList);
        readingListItemRepository.save(sourceItem);
    }

    private ReadingList findUserList(Long listId, User currentUser) {
        return readingListRepository.findByIdAndUserId(
                        listId,
                        currentUser.getId()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Liste bulunamadı veya bu işlem için yetkiniz yok."
                ));
    }
}
