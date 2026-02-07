package prs.fmtareco.adventure.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * handles all exceptions related with failed resource selection
     *
     * @param ex throw ed exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.NOT_FOUND, null);
    }

    /**
     * handles all exceptions related with failed resource selection
     *
     * @param ex thrown exception
     * @param request http request
     * @return Response w/ ErrorInfo
     */
    @ExceptionHandler(InvalidResourceException.class)
    public ResponseEntity<ErrorInfo> handleInvalidResource(
            InvalidResourceException ex,
            HttpServletRequest request) {
        return getErrorResponse(ex, request, HttpStatus.NOT_ACCEPTABLE, null);
    }

    /**
     * formats an error response
     * @param ex thrown exception
     * @param request http request
     * @param status http status code
     * @return Response w/ ErrorInfo
     */
    public ResponseEntity<ErrorInfo> getErrorResponse(
            Exception ex,
            HttpServletRequest request,
            HttpStatus status,
            Map<String, String> errors) {
        ErrorInfo info = getErrorInfo(ex, request, status, errors);
        return new ResponseEntity<>(info, status);
    }

    /**
     * return a record with the error relevant information
     * @param ex thrown exception
     * @param request http request
     * @param status http status code
     * @return error info structure
     */
    public ErrorInfo getErrorInfo(Exception ex, HttpServletRequest request, HttpStatus status, Map<String, String> errors)
    {
        return new ErrorInfo(ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                request.getRequestURI(),
                Instant.now(),
                errors
        );
    }
}
