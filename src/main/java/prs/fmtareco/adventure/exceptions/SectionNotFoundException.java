package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SectionNotFoundException extends ResourceNotFoundException {
    public SectionNotFoundException(Long bookId, Integer sectionNumber) {

        super("Section("+sectionNumber+" on Book("+bookId+") not found");
    }
}