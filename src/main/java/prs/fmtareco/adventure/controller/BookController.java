package prs.fmtareco.adventure.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prs.fmtareco.adventure.dtos.BookDetails;
import prs.fmtareco.adventure.dtos.BookRequest;
import prs.fmtareco.adventure.dtos.BookSummary;
import prs.fmtareco.adventure.dtos.CategoriesRequest;
import prs.fmtareco.adventure.service.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService svc) {
        this.bookService = svc;
    }

    /**
     * creates a new book based on the info on the request
     * @param req BookRequest structure correspondent to the json files
     * @return BooKDetails DTO with info of the new Book
     */
    @PostMapping
    public ResponseEntity<BookDetails> createBook(@Valid @RequestBody BookRequest req) {
        BookDetails newBook = bookService.createBook(req);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }


    /**
     * GET - /api/books{id}
     * fetches a single book details
     * @param id - identifies the book to be fetched
     * @return BookDetails with the fetched book content
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetails> getBookDetails(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getDetails(id));
    }

    /**
     * POST /api/books/{id}/categories/{name} -
     * associates the book association w/ a new category
     * Idempotent
     * @param id - identifies the book
     * @param categoryName - identifies the category
     */
    @PostMapping("/{id}/categories/{categoryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCategory(@PathVariable Long id, @PathVariable String categoryName) {
        bookService.addCategory(id, categoryName);
    }

    /**
     * POST /api/books/{id}/categories
     * associates the book association w/ a list of categories
     * @param id - identifies the book
     * @param request - identifies the category list
     */
    @PostMapping("/{id}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCategoriesList(@PathVariable Long id, @RequestBody CategoriesRequest request) {
        bookService.addCategories(id, request.categories());
    }

    /**
     * DELETE /api/books/{id}/categories/{name} -
     * eliminates the book association w/ particular category
     * @param id - identifies the book
     * @param categoryName - identifies the category the removes
     */
    @DeleteMapping("/{id}/categories/{categoryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long id, @PathVariable String categoryName) {
        bookService.removeCategory(id, categoryName);
    }

    /**
     * DELETE /api/books/{id}/categories
     * eliminates the book association w/ a list of categories
     * @param id - identifies the book
     * @param request - identifies the category list
     */
    @DeleteMapping("/{id}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategories(@PathVariable Long id,@RequestBody CategoriesRequest request) {
        bookService.removeCategories(id, request.categories());
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
     * @param ascending - indicates the sort direction
     * @return List of books (summary)
     */
    @GetMapping
    public Page<BookSummary> listAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false, defaultValue = "") String condition,
            @RequestParam(value = "page", defaultValue = "0" ) int page,
            @RequestParam(value = "size", defaultValue = "3" ) int size,
            @RequestParam  (defaultValue = "true") boolean ascending
    ) {
        Sort sort = getBooksSort(ascending,
                Optional.ofNullable(title),
                Optional.ofNullable(author));
        Pageable pageable = PageRequest.of(page, size, sort);
        return bookService.listAllFiltered(
                    Optional.ofNullable(title),
                    Optional.ofNullable(author),
                    Optional.ofNullable(category),
                    Optional.ofNullable(difficulty),
                    Optional.ofNullable(condition),
                    pageable
        );
    }

    /**
     * return the Books Sort criteria
     * @param ascending - indicates the input sort direction
     * @param title - the sort will be based on book title
     * @param author - when present, the sort will be based on author name
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

}
