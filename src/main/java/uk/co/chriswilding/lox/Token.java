package uk.co.chriswilding.lox;

record Token(TokenType type, String lexeme, Object literal, int line) {
}
