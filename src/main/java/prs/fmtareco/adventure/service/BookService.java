package prs.fmtareco.adventure.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.annotations.TrackExecution;
import prs.fmtareco.adventure.dtos.*;
import prs.fmtareco.adventure.exceptions.BookNotFoundException;
import prs.fmtareco.adventure.exceptions.DuplicateBookException;
import prs.fmtareco.adventure.exceptions.MissingValueException;
import prs.fmtareco.adventure.exceptions.SectionNotFoundException;
import prs.fmtareco.adventure.factory.BookFactory;
import prs.fmtareco.adventure.model.*;
import prs.fmtareco.adventure.repository.BookRepository;
import prs.fmtareco.adventure.repository.SectionRepository;

import java.util.List;
import java.util.Optional;

import static prs.fmtareco.adventure.repository.BookRepository.byFilters;

@Service
public class BookService {


    private final BookRepository bookRepo;
    private final SectionRepository sectionRepo;
    private final BookFactory factory;

    public BookService(
            BookRepository bookRepo,
            SectionRepository sectionRepo,
            BookFactory factory) {
        this.bookRepo = bookRepo;
        this.factory = factory;
        this.sectionRepo =  sectionRepo;
    }


    @Transactional
    @TrackExecution
    public BookDetails createBook(BookRequest request) {
        if (request.title()==null)
            throw new MissingValueException("title missing");
        if (request.author()==null)
            throw new MissingValueException("author missing");
        if (bookRepo.existsByTitleIgnoreCaseAndAuthorIgnoreCase(
                request.title(), request.author())) {
            throw new DuplicateBookException(request.title(), request.author());
        }

        Book book = factory.createBook(request);


        bookRepo.save(book);

        return toBookDetails(book);
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
    @TrackExecution
    public Page<BookSummary> listAllFiltered(
            String title, String author, String category,
            String difficulty, String condition,
            Pageable pageable) {
        return bookRepo.findAll(byFilters(title,author,category, difficulty, condition), pageable)
                .map(this::toBookSummary);
    }

    /**
     * fetches the details of the book with the argument id, from the input
     *
     * @param id - id of the book to be retrieved
     * @return BookDetails with the selected book content
     */
    public BookDetails getBookDetails(Long id) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return toBookDetails(book);
    }

    /**
     * fetches the details of the section with the argument id, from the input
     *
     * @param bookId - id of the book to be retrieved
     * @param sectionNumber - number of the section to be retrieved
     * @return BookDetails with the selected book content
     */
    public SectionDetails getSectionDetails(Long bookId, Integer sectionNumber) {
        Section section = sectionRepo.findByBookIdAndSectionNumber(bookId,sectionNumber)
                .orElseThrow(() -> new SectionNotFoundException(bookId,sectionNumber));
        return toSectionDetails(section);
    }

    /**
     * associates a category w/ the book w/ key id
     * @param id book key
     * @param categoryName category to associate
     */
    @Transactional
    public void addCategory(Long id, String categoryName) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        factory.addCategoryToBook(book, categoryName);
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
            factory.addCategoryToBook(book, categoryName);
        }
        bookRepo.save(book);
    }

    /**
     * disassociates a category from the book w/ key id
     * @param id book key
     * @param categoryName category to associate
     */
    @Transactional
    public void removeCategory(Long id, String categoryName) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        factory.removeCategoryFromBook(book, categoryName);
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
            factory.removeCategoryFromBook(book, categoryName);
        }
        bookRepo.save(book);
    }

    /**
     * executes a series of simulated games on the book, traversing all possible paths
     * to detect loops, number of successful/failure paths
     * @param id - id of the books
     * @return BookSimulation with the summary of the simulations
     */
    public BookSimulation simulateBookPaths(Long id) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        GameSimulation simulation = new GameSimulation(book);
        simulation.applyMoves();
        return BookSimulation.builder()
                .id(book.getId())
                .title(book.getTitle())
                .numberOfSuccessfulPaths(simulation.numberOfSuccessfulPaths())
                .numberOfLoopPaths(simulation.numberOfLoopPaths())
                .numberOfFailurePaths(simulation.numberOfFailedPaths())
                .build();
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
                                .toList())
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
                                .toList())
                .sections(
                        book.getSections().stream()
                                .map(this::toSectionSummary)
                                .toList())
                .difficulty(book.getDifficulty().toString())
                .build();
    }

    /**
     * to convert the arg section to a summary to display on the book details
     * @param section - argument section
     * @return - instance of SectionSummary
     */
    private SectionSummary toSectionSummary(Section section) {
        return SectionSummary.builder()
                .sectionNumber(section.getSectionNumber())
                .type(section.getType().toString())
                .text(section.getText())
                .build();
    }

    /**
     * to convert the arg section to a details info
     * @param section - argument section
     * @return - instance of SectionDetails
     */
    private SectionDetails toSectionDetails(Section section) {
        return SectionDetails.builder()
                .sectionNumber(section.getSectionNumber())
                .type(section.getType().toString())
                .text(section.getText())
                .options(
                        section.getOptions().stream()
                                .map(this::toOptionSummary)
                                .toList())
                .build();
    }

    /**
     * to convert the arg option to a summary DTO
     * @param option - argument section
     * @return - instance of OptionSummary
     */
    private OptionSummary toOptionSummary(Option option) {
        Optional<Consequence> csq = Optional.ofNullable(option.getConsequence());
        return OptionSummary.builder()
                .description(option.getDescription())
                .gotoSectionNumber(option.getGotoSectionNumber())
                .consequence(csq.map(Consequence::getText).orElse(""))
                .build();
    }

}
