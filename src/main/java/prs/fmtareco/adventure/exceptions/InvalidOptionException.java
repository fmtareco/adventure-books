package prs.fmtareco.adventure.exceptions;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class InvalidOptionException extends InvalidResourceException {
    public InvalidOptionException(int optionNo, int numOptions) {
        super("Invalid option("+optionNo+"), total: " + numOptions + ".");
    }
}