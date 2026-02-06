package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(String name) {
        super("Category("+name+") not found");
    }
}