package prs.fmtareco.adventure.loader.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import prs.fmtareco.adventure.factory.BookFactory;
import prs.fmtareco.adventure.loader.json.BookJson;
import prs.fmtareco.adventure.loader.json.SectionJson;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Section;
import prs.fmtareco.adventure.service.BookService;


@Component
@RequiredArgsConstructor
public class BookMapper {

    private final SectionMapper sectionMapper;
    private final BookFactory bookFactory;


    public Book fromJson(BookJson json) {
        Book book = Book.create(json.title(), json.author(),json.difficulty());
        fromJsonSections(book, json);
        fromJsonCategories(book, json);
        book.setBookCondition();
        return book;
    }

    private void fromJsonCategories(Book book, BookJson json) {
        if (json.categories() == null)
            return;
        for (String categoryName: json.categories()) {
            try {
                bookFactory.addCategoryToBook(book, categoryName);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void fromJsonSections(Book book, BookJson json) {
        if (json.sections() == null)
            return;
        for (SectionJson sJson : json.sections()) {
            try {
                Section section = sectionMapper.fromJson(sJson);
                book.addSection(section);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}

