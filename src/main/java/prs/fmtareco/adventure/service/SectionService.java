package prs.fmtareco.adventure.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import prs.fmtareco.adventure.dtos.BookSummary;
import prs.fmtareco.adventure.dtos.SectionSummary;
import prs.fmtareco.adventure.model.Book;
import prs.fmtareco.adventure.model.Category;
import prs.fmtareco.adventure.model.Section;
import prs.fmtareco.adventure.repository.BookRepository;
import prs.fmtareco.adventure.repository.SectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static prs.fmtareco.adventure.repository.BookRepository.byFilters;

@Service
public class SectionService {


    private final SectionRepository repo;

    public SectionService(SectionRepository repo) {
        this.repo = repo;
    }


    /**
     * to convert the selected Section to a
     * SectionSummary DTO to return on the book details
     * @param section - the section to be converted
     * @return SectionSummary
     */
    public SectionSummary toSectionSummary(Section section) {
        return SectionSummary.builder()
                .id(section.getId())
                .sectionNumber(section.getSectionNumber())
                .text(section.getText())
                .build();
    }


}
