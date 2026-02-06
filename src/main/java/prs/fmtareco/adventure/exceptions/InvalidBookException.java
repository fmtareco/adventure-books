package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidBookException extends RuntimeException {
    public InvalidBookException(Long id, String condition) {
        super("Invalid book("+id+") status: " + condition + ".");
    }
}