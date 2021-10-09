package uk.co.chriswilding.lox;

import lombok.Getter;

class RuntimeError extends RuntimeException {
    @Getter
    final Token token;

    RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
