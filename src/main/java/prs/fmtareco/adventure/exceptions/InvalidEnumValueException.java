package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidEnumValueException extends RuntimeException {
    public InvalidEnumValueException(String field, String value, String validValues) {
        super("Invalid value("+value+") for field[" + field + "]. Valid values: " + validValues);
    }
    public InvalidEnumValueException(String message) {
        super(message);
    }
}