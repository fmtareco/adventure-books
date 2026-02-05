package prs.fmtareco.adventure.loader;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import prs.fmtareco.adventure.loader.json.BookJson;
import prs.fmtareco.adventure.loader.mapper.BookMapper;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.repository.BookRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


import java.io.InputStream;
import java.util.List;

@Component
public class BooksLoader {

    private static final Logger log = LoggerFactory.getLogger(BooksLoader.class);

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;
    private final BookMapper bookMapper;

    public BooksLoader(
            BookRepository bookRepository,
            BookMapper bookMapper,
            ObjectMapper objectMapper
    ) {
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
        this.bookMapper = bookMapper;
    }

    @PostConstruct
    public void loadAllBooks() {
        try {
            Resource[] jsonFiles = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:books/lists/*.json");
            for (Resource jsonFile : jsonFiles) {
                loadMultipleBooksResource(jsonFile);
                System.out.println("loaded : " + jsonFile);
            }
        } catch (Exception e) {
            log.error("Failed to scan multiple books JSON files", e);
        }
        try {
            Resource[] jsonFiles = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:books/*.json");
            for (Resource jsonFile : jsonFiles) {
                loadSingleBookResource(jsonFile);
                System.out.println("loaded : " + jsonFile);
            }
        } catch (Exception e) {
            log.error("Failed to scan single book JSON files", e);
        }
    }


    private void loadMultipleBooksResource(Resource jsonFile)  {
        try (InputStream is = jsonFile.getInputStream()) {
            if (is.available() == 0) {
                log.error("Empty resource file : {}", jsonFile.getFilename());
                return;
            }
            List<BookJson> booksList =
                    objectMapper.readValue(is, new TypeReference<List<BookJson>>() {});
            for(BookJson bj : booksList) {
                loadBook(bj);
            }
        } catch (Exception e) {
            log.error("Failed to load books from {}", jsonFile.getFilename(), e);
        }
    }
    private void loadSingleBookResource(Resource jsonFile)  {
        try (InputStream is = jsonFile.getInputStream()) {
            if (is.available() == 0) {
                log.error("Empty resource file : {}", jsonFile.getFilename());
                return;
            }
            BookJson bookJson = objectMapper.readValue(is, BookJson.class);
            loadBook(bookJson);
        } catch (Exception e) {
            log.error("Failed to load book from {}", jsonFile.getFilename(), e);
        }
    }
    private void loadBook(BookJson bookJson)  {
        String title = bookJson.title();
        String author = bookJson.author();
        try {
            if (bookRepository
                    .findByTitleIgnoreCaseAndAuthorIgnoreCase(title, author)
                    .isPresent()) {
                log.info("Skipped book: {} by {}", title, author);
                return;
            }
            Book book = bookMapper.fromJson(bookJson);
            try {
                bookRepository.save(book);
            } catch (Exception e) {
                log.error("Failed to save book: {} by {}", title, author, e);
            }
            log.info("Loaded book: {} by {}", title, author);
        } catch (Exception e) {
            log.error("Failed to load book {} by {}", title, author, e);
        }
    }
}
