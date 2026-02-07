package prs.fmtareco.adventure.factory;

import org.springframework.stereotype.Component;
import prs.fmtareco.adventure.dtos.BookRequest;
import prs.fmtareco.adventure.dtos.ConsequenceRequest;
import prs.fmtareco.adventure.dtos.SectionRequest;
import prs.fmtareco.adventure.dtos.OptionRequest;
import prs.fmtareco.adventure.exceptions.CategoryNotFoundException;
import prs.fmtareco.adventure.model.*;
import prs.fmtareco.adventure.repository.BookRepository;
import prs.fmtareco.adventure.repository.CategoryRepository;

@Component
public class BookFactory {

    private final BookRepository bookRepo;
    private final CategoryRepository categoryRepo;

    public BookFactory(
            BookRepository bookRepo,
            CategoryRepository  categoryRepo) {
        this.bookRepo = bookRepo;
        this.categoryRepo = categoryRepo;
    }

    public Book fromRequest(BookRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .difficulty(Book.Difficulty.from(request.difficulty()))
                .build();
        if (request.categories() != null) {
            request.categories().forEach(c -> addCategoryToBook(book, c));
        }
        if (request.sections() != null) {
            request.sections().forEach(sectionReq -> {
                Section section = createSection(sectionReq );
                book.addSection(section);
            });
        }
        book.setBookCondition();
        return book;
    }

    private Section createSection(SectionRequest req) {
        Section section = Section.builder()
                .sectionNumber(req.id())
                .text(req.text())
                .type(Section.Type.from(req.type()))
                .build();
        if (req.options() != null) {
            req.options().forEach(optReq -> {
                Option option = createOption(optReq);
                section.addOption(option);
            });
        }
        return section;
    }

    private Option createOption(OptionRequest req) {
        Option option = Option.builder()
                .description(req.description())
                .gotoSectionNumber(req.gotoId())
                .build();
        if (req.consequence() != null) {
            option.setConsequence(createConsequence(req.consequence(), option));
        }
        return option;
    }

    private Consequence createConsequence(ConsequenceRequest req, Option option) {
        return Consequence.builder()
                .type(Consequence.Type.from(req.type()))
                .value(req.value())
                .text(req.text())
                .option(option)
                .build();
    }

    /**
     * associates a category w/ the book
     *
     * @param book book
     * @param categoryName category to associate
     */
    public void addCategoryToBook(Book book, String categoryName) {
        Category category = categoryRepo
                .findByNameIgnoreCase(categoryName)
                .orElseGet(() -> categoryRepo.save(new Category(categoryName)));
        book.addCategory(category);
    }
    /**
     * dissociates a category from the book
     *
     * @param book book
     * @param categoryName category to associate
     */
    public void removeCategoryFromBook(Book book, String categoryName) {
        Category category = categoryRepo
                .findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(categoryName));
        book.removeCategory(category);
    }
}


