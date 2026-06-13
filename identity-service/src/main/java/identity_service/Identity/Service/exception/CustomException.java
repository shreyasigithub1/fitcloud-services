package identity_service.Identity.Service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
//This is needed when no handler is needed
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
