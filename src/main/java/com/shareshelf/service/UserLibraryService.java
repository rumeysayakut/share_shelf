
package com.shareshelf.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shareshelf.dto.request.UserLibraryRequestDto;
import com.shareshelf.dto.response.UserLibraryResponseDto;
import com.shareshelf.entity.Book;
import com.shareshelf.entity.ReadingStatus;
import com.shareshelf.entity.User;
import com.shareshelf.entity.UserLibrary;
import com.shareshelf.mapper.UserLibraryMapper;
import com.shareshelf.repository.BookRepository;
import com.shareshelf.repository.UserLibraryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLibraryService {

    private final UserLibraryRepository userLibraryRepository;
    private final BookRepository bookRepository;
    private final UserLibraryMapper userLibraryMapper;

    public UserLibraryResponseDto addBook(UserLibraryRequestDto request, User currentUser) {

        if (request.getBookId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kitap ID boş olamaz.");
        }

        if (userLibraryRepository.existsByUserIdAndBookId(
                currentUser.getId(), request.getBookId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu kitap zaten kitaplığınızda bulunuyor.");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kitap bulunamadı."));

        UserLibrary userLibrary = userLibraryMapper.toEntity(request);
        userLibrary.setUser(currentUser);
        userLibrary.setBook(book);

        if (request.getReadingStatus() == null) {
            userLibrary.setReadingStatus(ReadingStatus.WANT_TO_READ);
        } else {
            userLibrary.setReadingStatus(request.getReadingStatus());
        }

        return userLibraryMapper.toResponseDto(userLibraryRepository.save(userLibrary));
    }

    public List<UserLibraryResponseDto> getMyLibrary(User currentUser) {

        return userLibraryRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(userLibraryMapper::toResponseDto)
                .toList();
    }

    public UserLibraryResponseDto updateReadingStatus(
            Long bookId,
            ReadingStatus readingStatus,
            User currentUser) {

        if (readingStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Okuma durumu boş olamaz.");
        }

        UserLibrary userLibrary = userLibraryRepository
                .findByUserIdAndBookId(currentUser.getId(), bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kitap kitaplığınızda bulunamadı."));

        userLibrary.setReadingStatus(readingStatus);

        return userLibraryMapper.toResponseDto(userLibraryRepository.save(userLibrary));
    }

    public void removeBook(Long bookId, User currentUser) {

        UserLibrary userLibrary = userLibraryRepository
                .findByUserIdAndBookId(currentUser.getId(), bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kitap kitaplığınızda bulunamadı."));

        userLibraryRepository.delete(userLibrary);
    }
}
