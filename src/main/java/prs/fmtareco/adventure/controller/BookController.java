package prs.fmtareco.adventure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prs.fmtareco.adventure.dtos.BookResponse;
import prs.fmtareco.adventure.service.BookService;

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
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(
                service.listAllFiltered(
                    Optional.ofNullable(title),
                    Optional.ofNullable(author),
                    Optional.ofNullable(category),
                    Optional.ofNullable(difficulty)
                )
        );
    }

}
