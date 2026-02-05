package prs.fmtareco.adventure.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import prs.fmtareco.adventure.dtos.BookResponse;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.repository.BookRepository;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {


    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<BookResponse> listAllBooks() {
        return repo.findAll()
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
                .categories(book.getCategories())
                .build();
    }

}
