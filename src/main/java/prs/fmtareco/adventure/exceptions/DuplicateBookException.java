package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class DuplicateBookException extends InvalidResourceException {
    public DuplicateBookException(String title, String author) {
        super("Invalid book("+title+") from("+author+") + already exists.");
    }
}