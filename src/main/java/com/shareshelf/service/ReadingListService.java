
package com.shareshelf.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.shareshelf.dto.request.ReadingListItemRequestDto;
import com.shareshelf.dto.request.ReadingListRequestDto;
import com.shareshelf.dto.response.ReadingListItemResponseDto;
import com.shareshelf.dto.response.ReadingListResponseDto;
import com.shareshelf.entity.Book;
import com.shareshelf.entity.ReadingList;
import com.shareshelf.entity.ReadingListItem;
import com.shareshelf.entity.User;
import com.shareshelf.mapper.ReadingListItemMapper;
import com.shareshelf.mapper.ReadingListMapper;
import com.shareshelf.repository.BookRepository;
import com.shareshelf.repository.ReadingListItemRepository;
import com.shareshelf.repository.ReadingListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReadingListService {

    private final ReadingListRepository readingListRepository;
    private final ReadingListItemRepository readingListItemRepository;
    private final BookRepository bookRepository;
    private final ReadingListMapper readingListMapper;
    private final ReadingListItemMapper readingListItemMapper;

    public ReadingListResponseDto createList(ReadingListRequestDto request, User currentUser) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Liste adı boş olamaz.");
        }

        ReadingList readingList = readingListMapper.toEntity(request);
        readingList.setUser(currentUser);
        readingList.setName(request.getName().trim());

        if (readingList.getIsPublic() == null) {
            readingList.setIsPublic(false);
        }

        return readingListMapper.toResponseDto(readingListRepository.save(readingList));
    }

    public List<ReadingListResponseDto> getMyLists(User currentUser) {

        return readingListRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(readingListMapper::toResponseDto)
                .toList();
    }

    public ReadingListResponseDto getListById(Long listId, User currentUser) {

        ReadingList readingList = findUserList(listId, currentUser);

        return readingListMapper.toResponseDto(readingList);
    }

    public ReadingListResponseDto updateList(
            Long listId,
            ReadingListRequestDto request,
            User currentUser) {

        ReadingList readingList = findUserList(listId, currentUser);

        if (request.getName() != null) {
            if (request.getName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Liste adı boş olamaz.");
            }
            readingList.setName(request.getName().trim());
        }

        if (request.getDescription() != null) {
            readingList.setDescription(request.getDescription().trim());
        }

        if (request.getIsPublic() != null) {
            readingList.setIsPublic(request.getIsPublic());
        }

        return readingListMapper.toResponseDto(readingListRepository.save(readingList));
    }

    @Transactional
    public void deleteList(Long listId, User currentUser) {

        ReadingList readingList = findUserList(listId, currentUser);

        readingListItemRepository.deleteAllByReadingListId(readingList.getId());
        readingListRepository.delete(readingList);
    }

    public ReadingListItemResponseDto addBookToList(
            Long listId,
            ReadingListItemRequestDto request,
            User currentUser) {

        ReadingList readingList = findUserList(listId, currentUser);

        if (request.getBookId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kitap ID boş olamaz.");
        }

        if (readingListItemRepository.existsByReadingListIdAndBookId(listId, request.getBookId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu kitap zaten listede bulunuyor.");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kitap bulunamadı."));

        ReadingListItem item = readingListItemMapper.toEntity(request);
        item.setReadingList(readingList);
        item.setBook(book);

        return readingListItemMapper.toResponseDto(readingListItemRepository.save(item));
    }

    public List<ReadingListItemResponseDto> getListBooks(Long listId, User currentUser) {

        findUserList(listId, currentUser);

        return readingListItemRepository.findAllByReadingListId(listId)
                .stream()
                .map(readingListItemMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void removeBookFromList(Long listId, Long bookId, User currentUser) {

        findUserList(listId, currentUser);

        ReadingListItem item = readingListItemRepository
                .findByReadingListIdAndBookId(listId, bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kitap bu listede bulunamadı."));

        readingListItemRepository.delete(item);
    }

    private ReadingList findUserList(Long listId, User currentUser) {

        return readingListRepository.findByIdAndUserId(listId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Okuma listesi bulunamadı."));
    }
}
