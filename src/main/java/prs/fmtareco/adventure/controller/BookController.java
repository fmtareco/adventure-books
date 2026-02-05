package prs.fmtareco.adventure.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prs.fmtareco.adventure.dtos.BookResponse;
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

    @GetMapping
    public ResponseEntity<List<BookResponse>> listAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "true") boolean onlyValid,
            @RequestParam(value = "page", defaultValue = "0" ) int page,
            @RequestParam(value = "size", defaultValue = "3" ) int size,
            @RequestParam  (defaultValue = "true") boolean ascending
    ) {
        Sort sort = getSort(ascending,
                Optional.ofNullable(title),
                Optional.ofNullable(author));
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                service.listAllFiltered(
                    Optional.ofNullable(title),
                    Optional.ofNullable(author),
                    Optional.ofNullable(category),
                    Optional.ofNullable(difficulty),
                    onlyValid,
                    pageable
                )
        );
    }

    /**
     * return the Sort criteria
     * @param ascending - indicates the input sort direction
     * @return Sort criteria
     */
    protected Sort getSort(
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
