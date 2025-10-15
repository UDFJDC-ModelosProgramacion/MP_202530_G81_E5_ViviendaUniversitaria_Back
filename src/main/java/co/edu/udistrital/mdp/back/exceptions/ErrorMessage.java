package co.edu.udistrital.mdp.back.exceptions;

public enum ErrorMessage {
    PROPIETARIO_NOT_FOUND("El propietario no existe"),
    VIVIENDA_NOT_FOUND("La vivienda no existe"),
    MULTIMEDIA_NOT_FOUND("El archivo multimedia no existe"),
    UNIVERSIDAD_CERCA_NOT_FOUND("La universidad cerca no existe"),
    UNIVERSIDAD_NOT_FOUND("La universidad no existe");

    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}