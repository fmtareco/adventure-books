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
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

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
                    .getResources("classpath:books/*.json");
            for (Resource jsonFile : jsonFiles) {
                loadBook(jsonFile);
                System.out.println(jsonFile);
            }
        } catch (Exception e) {
            log.error("Failed to scan book JSON files", e);
        }
    }


    private void loadBook(Resource jsonFile)  {
        try (InputStream is = jsonFile.getInputStream()) {
            if (is.available() == 0) {
                log.error("Empty resource file : {}", jsonFile.getFilename());
                return;
            }
            BookJson bookJson = objectMapper.readValue(is, BookJson.class);
            String title = bookJson.title();
            String author = bookJson.author();
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
            log.error("Failed to load book from {}", jsonFile.getFilename(), e);
        }
    }
}
