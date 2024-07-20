package com.anderson.planner.infra;

import com.anderson.planner.activities.exceptions.ActivityDateException;
import com.anderson.planner.activities.exceptions.ActivityNotFoundException;
import com.anderson.planner.link.exceptions.LinkNotFoundException;
import com.anderson.planner.participant.exceptions.ParticipantNotFoundException;
import com.anderson.planner.trip.exceptions.CreateTripDateException;
import com.anderson.planner.trip.exceptions.TripNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends RuntimeException {
    @ExceptionHandler(CreateTripDateException.class)
    private ResponseEntity<String> createTripDateHandler(CreateTripDateException createTripDateException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createTripDateException.getMessage());
    }

    @ExceptionHandler(ActivityDateException.class)
    private ResponseEntity<String> activityDateHandler(ActivityDateException activityDateException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(activityDateException.getMessage());
    }

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<String> handleTripNotFoundException(TripNotFoundException tripNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tripNotFoundException.getMessage());
    }

    @ExceptionHandler(ActivityNotFoundException.class)
    public ResponseEntity<String> handleActivityNotFoundException(ActivityNotFoundException activityNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(activityNotFoundException.getMessage());
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ResponseEntity<String> handleParticipantNotFoundException(ParticipantNotFoundException participantNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(participantNotFoundException.getMessage());
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<String> handleLinkNotFoundException(LinkNotFoundException linkNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(linkNotFoundException.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        String errorMessage = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(objectError -> objectError.getDefaultMessage())
                .findFirst() // Pegando o primeiro erro encontrado
                .orElse("Validation error"); // Mensagem padrão caso não haja erro

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
