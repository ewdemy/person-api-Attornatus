package com.mrcruz.personapi.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler{

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrroResponse handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest req){
        return getResponse(req, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrroResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest req){
        return getResponse(req, "Endereço atribuido a uma pessoa, não é possível excluir!", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrroResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest req){
        List<ErrroResponse.Field> fields = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(err -> {
            String nome = ((FieldError) err).getField();
            String mensagem = err.getDefaultMessage();
            fields.add(new ErrroResponse.Field(nome, mensagem));
        });

        ErrroResponse response = getResponse(req, "Um ou mais campos inválidos, preencha corretamente!", HttpStatus.BAD_REQUEST);
        response.setFields(fields);

        return response;
    }


    private ErrroResponse getResponse(HttpServletRequest req, String mensagem, HttpStatus status){
        ErrroResponse errroResponse = new ErrroResponse();
        errroResponse.setStatus(status.value());
        errroResponse.setMensagem(mensagem);
        errroResponse.setTimestamp(LocalDateTime.now());
        errroResponse.setPath(req.getRequestURI());

        return errroResponse;
    }
}
