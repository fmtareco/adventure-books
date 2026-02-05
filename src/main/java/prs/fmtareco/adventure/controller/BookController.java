package prs.fmtareco.adventure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prs.fmtareco.adventure.dtos.BookResponse;
import prs.fmtareco.adventure.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService svc) {
        this.service = svc;
    }

    @GetMapping
    public List<BookResponse> listAllBooks() {
        return service.listAllBooks();
    }

}
