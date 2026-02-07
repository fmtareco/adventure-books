package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidSectionException extends InvalidResourceException {
    public InvalidSectionException(int sectionNo) {
        super("Invalid section("+sectionNo+").");
    }
}