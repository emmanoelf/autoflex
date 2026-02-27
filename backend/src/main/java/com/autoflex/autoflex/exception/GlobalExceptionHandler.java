package com.autoflex.autoflex.exception;

import com.autoflex.autoflex.dto.ProblemDetail;
import com.autoflex.autoflex.dto.ProblemType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<ProblemDetail> handleNotFoundException(NotFoundException ex){
        return buildProblemDetailResponse(HttpStatus.NOT_FOUND, ProblemType.RESOURCE_NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex){
        return buildProblemDetailResponse(HttpStatus.BAD_REQUEST, ProblemType.INVALID_DATA, ex);
    }

    private ResponseEntity<ProblemDetail> buildProblemDetailResponse(HttpStatus status, ProblemType problemType, Exception ex){
        ProblemDetail problemDetail = this.createProblemDetail(status, problemType, ex.getMessage());
        return ResponseEntity.status(status).body(problemDetail);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, ProblemType problemType, String detail){
        return ProblemDetail.builder()
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail)
                .build();
    }
}
