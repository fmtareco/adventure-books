package prs.fmtareco.adventure.loader.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import prs.fmtareco.adventure.loader.json.BookJson;
import prs.fmtareco.adventure.loader.json.SectionJson;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Section;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final SectionMapper sectionMapper;

    public Book fromJson(BookJson json) {
        Book book = new Book();
        book.setTitle(json.title());
        book.setAuthor(json.author());
        book.setDifficulty(json.difficulty());

        if (json.sections() != null) {
            for (SectionJson sJson : json.sections()) {
                Section section = sectionMapper.fromJson(sJson);
                book.addSection(section);
            }
        }

        return book;
    }
}