package uk.co.codelity.inventory.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StockExceptionController {

    @ExceptionHandler(value = OutOfStockException.class)
    public ResponseEntity<Object> exception(OutOfStockException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }

}
