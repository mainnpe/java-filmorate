package ru.yandex.practicum.filmorate.exception;

public abstract class NotFoundException extends Exception{
    public NotFoundException(String message) {
        super(message);
    }
}
