package com.techmind.api.exceptions;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GestorDeErrores {

    //parametros vacios o faltantes
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DatosError> gestionarError400(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        var errores = ex.getFieldErrors()
            .stream()
            .map(DatosError400Detalles::new)
            .toList();

        return ResponseEntity.badRequest().body(
            new DatosError(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                request.getRequestURI(),
                errores
            )
        );
    }


    //peticion sin body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DatosError> gestionarError400(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest()
                .body(new DatosError(
                    HttpStatus.BAD_REQUEST,
                    "El cuerpo de la petición es obligatorio",
                    request.getRequestURI()
                ));
    }


    //entidad no encontrada en la aplicación
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DatosError> gestionarError404(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new DatosError(
                HttpStatus.NOT_FOUND,
                "Contenido no encontrado",
                request.getRequestURI()
            ));
    }


    //endpoint o recurso HTTP no encontrado
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<DatosError> gestionarError404Recurso(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DatosError(
                    HttpStatus.NOT_FOUND,
                    "Endpoint o recurso HTTP no encontrado",
                    request.getRequestURI()
                ));
    }


    //metodo o verbo HTTP no permitido 
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DatosError> gestionarError405(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(new DatosError(
                HttpStatus.METHOD_NOT_ALLOWED,
                ex.getMessage(),
                request.getRequestURI()
            ));
    }


    //Cuerpo o body no soportado
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<DatosError> gestionarError415(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(new DatosError(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Formato de contenido no soportado",//ex.getMessage(),
                request.getRequestURI()
            ));
    }


    //servicio externo no disponible
    @ExceptionHandler(ServicioInferenciaException.class)
    public ResponseEntity<DatosError> gestionarError503(
            ServicioInferenciaException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new DatosError(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio de clasificación no disponible",
                request.getRequestURI()
            ));
    }


    //errores no controlados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DatosError> gestionarError500(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DatosError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocurrió un error inesperado",
                        request.getRequestURI()
                ));
    }


    //Datos de errores uniforme
    public record DatosError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Object detalles
    ) {
        public DatosError(HttpStatus status, String message, String path, Object detalles) {
            this(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                detalles
            );
        }

        public DatosError(HttpStatus status, String message, String path) {
            this(status, message, path, null);
        }
    }


    //Campos faltantes
    public record DatosError400Detalles(
        String campo, 
        String mensaje){
        public DatosError400Detalles(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }

}
