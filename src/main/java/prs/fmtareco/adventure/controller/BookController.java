package prs.fmtareco.adventure.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prs.fmtareco.adventure.dtos.BookDetails;
import prs.fmtareco.adventure.dtos.BookSummary;
import prs.fmtareco.adventure.dtos.SectionSummary;
import prs.fmtareco.adventure.service.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService svc) {
        this.service = svc;
    }




    /**
     * GET - /api/books{id}
     * fetches a single book details
     * @param id - identifies the book to be fetched
     * @return BookDetails with the fetched book content
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetails> getBookDetails(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDetails(id));
    }

    /**
     * GET - /api/books{id}
     * fetches a list of SectionSummary with the sections of identified book
     * @param id - identifies the book
     * @param page - determines the page from where the list starts
     * @param size - determines the number of books returned
     * @return List of SectionSummary
     */
    @GetMapping("/{id}/sections")
    public ResponseEntity<List<SectionSummary>> getBookSections(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0" ) int page,
            @RequestParam(value = "size", defaultValue = "10" ) int size,
            @RequestParam  (defaultValue = "true") boolean ascending) {
        Sort sort = getSectionsSort(ascending);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                service.listAllSections(id, pageable)
        );
    }



    /**
     * GET  - /api/books
     * fetches a list of BookSummary with the books matching the conditions
     * determined by the API query params.
     * Also sets the pagination and sorting settings
     *
     * @param title - when present, filters books with title matching this arg
     * @param author - when present, filters books with author matching this arg
     * @param category - when present, filters books with this category
     * @param difficulty - when present, filters books with this difficulty level
     * @param condition - when present, filters books on this condition state
     * @param page - determines the page from where the list starts
     * @param size - determines the number of books returned
     * @return List of books (summary)
     */
    @GetMapping
    public ResponseEntity<List<BookSummary>> listAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false, defaultValue = "OK") String condition,
            @RequestParam(value = "page", defaultValue = "0" ) int page,
            @RequestParam(value = "size", defaultValue = "3" ) int size,
            @RequestParam  (defaultValue = "true") boolean ascending
    ) {
        Sort sort = getBooksSort(ascending,
                Optional.ofNullable(title),
                Optional.ofNullable(author));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                service.listAllFiltered(
                    Optional.ofNullable(title),
                    Optional.ofNullable(author),
                    Optional.ofNullable(category),
                    Optional.ofNullable(difficulty),
                    Optional.ofNullable(condition),
                    pageable
                )
        );
    }

    /**
     * return the Books Sort criteria
     * @param ascending - indicates the input sort direction
     * @param title - the sort will based on book title
     * @param author - when present, the sort will based on author name
     * @return Sort criteria
     */
    protected Sort getBooksSort(
            boolean ascending,
            Optional<String> title,
            Optional<String> author
            ) {
        Sort.Direction direction = ascending ?
                Sort.Direction.ASC:
                Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        if (title.isPresent() || !author.isPresent()) {
            Sort.Order titleOrder = new Sort.Order(direction, "title");
            orders.add(titleOrder);
        }
        if (author.isPresent()) {
            Sort.Order authorOrder = new Sort.Order(direction, "author");
            orders.add(authorOrder);
        }
        return Sort.by(orders);
    }

    /**
     * return the Sections Sort criteria
     * @param ascending - indicates the input sort direction
     * @return Sort criteria
     */
    protected Sort getSectionsSort(
            boolean ascending
    ) {
        Sort.Direction direction = ascending ?
                Sort.Direction.ASC:
                Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(direction, "sectionNumber"));
        return Sort.by(orders);
    }

}
