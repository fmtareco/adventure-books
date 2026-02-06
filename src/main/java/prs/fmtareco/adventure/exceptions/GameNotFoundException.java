package prs.fmtareco.adventure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameNotFoundException extends ResourceNotFoundException {
    public GameNotFoundException(Long id) {
        super("Game("+id+") not found");
    }
}