package prs.fmtareco.adventure.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.dtos.BookResponse;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Category;
import prs.fmtareco.adventure.repository.BookRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static prs.fmtareco.adventure.repository.BookRepository.byFilters;

@Service
public class BookService {


    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<BookResponse> listAll() {
        return repo.findAll()
                .stream()
                .map(this::toBookResponse)
                .toList();

    }

    public List<BookResponse> listAllFiltered(
            Optional<String> title,
            Optional<String> author,
            Optional<String> category,
            Optional<String> difficulty,
            Optional<String> condition,
            Pageable pageable) {
        return repo.findAll(byFilters(title,author,category, difficulty, condition), pageable)
                .stream()
                .map(this::toBookResponse)
                .toList();
    }



    //---------------------------------------------------------------------------------------//
    //                              internal utility methods                                 //
    //---------------------------------------------------------------------------------------//



    /**
     * to convert the selected Book to a
     * BookResponse DTO to return to the API caller
     * @param book - the book to be converted
     * @return BookResponse
     */
    private BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .author(book.getAuthor())
                .title(book.getTitle())
                .condition(book.getCondition().toString())
                .categories(
                        book.getCategories().stream()
                                .map(Category::getName)
                                .collect(Collectors.toList()))
                .difficulty(book.getDifficulty().toString())
                .build();
    }


}
