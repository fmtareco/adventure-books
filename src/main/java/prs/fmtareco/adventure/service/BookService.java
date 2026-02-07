package prs.fmtareco.adventure.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.dtos.BookDetails;
import prs.fmtareco.adventure.dtos.BookSummary;
import prs.fmtareco.adventure.exceptions.BookNotFoundException;
import prs.fmtareco.adventure.exceptions.CategoryNotFoundException;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Category;
import prs.fmtareco.adventure.repository.BookRepository;
import prs.fmtareco.adventure.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static prs.fmtareco.adventure.repository.BookRepository.byFilters;

@Service
public class BookService {


    private final BookRepository bookRepo;
    private final CategoryRepository categoryRepo;

    public BookService(
            BookRepository bookRepo,
            CategoryRepository  categoryRepo) {
        this.bookRepo = bookRepo;
        this.categoryRepo = categoryRepo;
    }

    /**
     * returns a list of BookSummary with the books matching the conditions
     * determined by the arguments.
     * Also handles the pagination and sorting settings from the pageable argument
     *
     * @param title - when present, filters books with title matching this arg
     * @param author - when present, filters books with author matching this arg
     * @param category - when present, filters books with this category
     * @param difficulty - when present, filters books with this difficulty level
     * @param condition - when present, filters books on this condition state
     * @param pageable - handles the pagination and sorting settings
     * @return List of books (summary)
     */
    public List<BookSummary> listAllFiltered(
            Optional<String> title,
            Optional<String> author,
            Optional<String> category,
            Optional<String> difficulty,
            Optional<String> condition,
            Pageable pageable) {
        return bookRepo.findAll(byFilters(title,author,category, difficulty, condition), pageable)
                .stream()
                .map(this::toBookSummary)
                .toList();
    }

    /**
     * fetches the details of the book with the argument id, from the input
     *
     * @param id - id of the book to be retrieved
     * @return BookDetails with the selected book content
     */
    public BookDetails getDetails(Long id) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return toBookDetails(book);
    }

    /**
     * associates a category w/ the book w/ key id
     * @param id book key
     * @param categoryName category to associate
     */
    @Transactional
    public void addCategory(Long id, String categoryName) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        addCategory(book, categoryName);
        bookRepo.save(book);
    }

    /**
     * associates a list of categories w/ the book w/ key id
     * @param id book key
     * @param categories list of categories to associate
     */
    @Transactional
    public void addCategories(Long id, List<String> categories) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        for(String categoryName : categories) {
            addCategory(book, categoryName);
        }
        bookRepo.save(book);
    }

    /**
     * associates a category w/ the book
     *
     * @param book book
     * @param categoryName category to associate
     */
    public void addCategory(Book book, String categoryName) {
        Category category = categoryRepo
                .findByNameIgnoreCase(categoryName)
                .orElseGet(() -> categoryRepo.save(new Category(categoryName)));
        book.addCategory(category);
    }

    /**
     * disassociates a category from the book w/ key id
     * @param id book key
     * @param categoryName category to associate
     */
    @Transactional
    public void removeCategory(Long id, String categoryName) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        removeCategory(book, categoryName);
        bookRepo.save(book);
    }

    /**
     * disassociates a list of categories from the book w/ key id
     * @param id book key
     * @param categories list of categories to associate
     */
    @Transactional
    public void removeCategories(Long id, List<String> categories) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        for(String categoryName : categories) {
            removeCategory(book, categoryName);
        }
        bookRepo.save(book);
    }

    /**
     * dissociates a category from the book
     *
     * @param book book
     * @param categoryName category to associate
     */
    public void removeCategory(Book book, String categoryName) {
        Category category = categoryRepo
                .findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName));
        book.removeCategory(category);
    }


    /**
     * to convert the selected Book to a
     * BookSummary DTO to return on the books list
     * @param book - the book to be converted
     * @return BookSummary
     */
    private BookSummary toBookSummary(Book book) {
        return BookSummary.builder()
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

    /**
     * to convert the selected Book to a
     * BookDetails DTO to return on the books details query
     * @param book - the book to be converted
     * @return BookDetails
     */
    private BookDetails toBookDetails(Book book) {
        return BookDetails.builder()
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
