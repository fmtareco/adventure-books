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

    public Book createBook(BookRequest request) {
        Book book = Book.create(
                request.title(),
                request.author(),
                Book.Difficulty.from(request.difficulty()));
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
        Section section = Section.create(
                req.id(),
                req.text(),
                Section.Type.from(req.type()));
        if (req.options() != null) {
            req.options().forEach(optReq -> {
                Option option = createOption(optReq);
                section.addOption(option);
            });
        }
        return section;
    }

    private Option createOption(OptionRequest req) {
        Option option = Option.create(req.description(), req.gotoId());
        if (req.consequence() != null) {
            option.setOptionConsequence(createConsequence(req.consequence()));
        }
        return option;
    }

    private Consequence createConsequence(ConsequenceRequest req) {
        return Consequence.create(
                Consequence.Type.from(req.type()),
                req.value(),
                req.text());
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


