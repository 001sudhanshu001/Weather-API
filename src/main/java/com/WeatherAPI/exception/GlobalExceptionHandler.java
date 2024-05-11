package com.WeatherAPI.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorDTO.setPath(request.getServletPath());

        log.error(ex.getMessage(), ex);

        return errorDTO;
    }
    @ExceptionHandler({BadRequestException.class, GeoLocationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.addError(ex.getMessage());
        errorDTO.setPath(request.getServletPath());

        log.error(ex.getMessage(), ex);

        return errorDTO;
    }

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
        errorDTO.addError(ex.getMessage());
        errorDTO.setPath(request.getServletPath());

        log.error(ex.getMessage(), ex);

        return errorDTO;
    }

//    @ExceptionHandler(ConstraintViolationException.class) // From javax.validation
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex){
//        ErrorDTO errorDTO = new ErrorDTO();
//
//        errorDTO.setTimestamp(new Date());
//        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorDTO.setPath(request.getServletPath());
//
//        ConstraintViolationException violationException = (ConstraintViolationException)ex;
//
//        var constraintViolations = violationException.getConstraintViolations();
//        // This exception will be invoked by List<HourlyWeather> so we have to do this for every
//        constraintViolations.forEach(constraint -> {
//            errorDTO.addError(constraint.getPropertyPath() + " : " + constraint.getMessage());
//        });
//
//        LOGGER.error(ex.getMessage(), ex);
//
//        return errorDTO;
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex) {
        ErrorDTO error = new ErrorDTO();

        ConstraintViolationException violationException = (ConstraintViolationException) ex;

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(request.getServletPath());

        var constraintViolations = violationException.getConstraintViolations();

        constraintViolations.forEach(constraint -> {
            error.addError(constraint.getPropertyPath() + ": " + constraint.getMessage());
        });

        log.error(ex.getMessage(), ex);

        return error;
    }

      /*
     @ExceptionHandler(MethodArgumentNotValidException.class) // provided by Spring
     public ResponseEntity<Map<String, String>> handelMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String,String> resp = new HashMap<>();

        // It will give all error in every field
        ex.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError) error).getField();

            String message = error.getDefaultMessage();

            resp.put(fieldName, message); // konsi field pe konsa error hai
        });

        return new ResponseEntity<Map<String, String>>(resp,HttpStatus.BAD_REQUEST);
     }
    */

    @Override // 400 -> Bad Request
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(((ServletWebRequest)request).getRequest().getServletPath());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        fieldErrors.forEach(fieldError -> {
            error.addError(fieldError.getDefaultMessage());
        });

        return new ResponseEntity<>(error, headers, status);
    }
}
