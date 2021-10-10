package uk.co.chriswilding.lox;

import lombok.RequiredArgsConstructor;
import uk.co.chriswilding.lox.expr.Assign;
import uk.co.chriswilding.lox.expr.Binary;
import uk.co.chriswilding.lox.expr.Expr;
import uk.co.chriswilding.lox.expr.Grouping;
import uk.co.chriswilding.lox.expr.Literal;
import uk.co.chriswilding.lox.expr.Logical;
import uk.co.chriswilding.lox.expr.Unary;
import uk.co.chriswilding.lox.expr.Variable;
import uk.co.chriswilding.lox.stmt.Block;
import uk.co.chriswilding.lox.stmt.Expression;
import uk.co.chriswilding.lox.stmt.If;
import uk.co.chriswilding.lox.stmt.Print;
import uk.co.chriswilding.lox.stmt.Stmt;
import uk.co.chriswilding.lox.stmt.Var;
import uk.co.chriswilding.lox.stmt.While;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    List<Stmt> parse() {
        var statements = new ArrayList<Stmt>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Expr and() {
        var expr = equality();

        while (match(TokenType.AND)) {
            var operator = previous();
            var right = equality();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr assignment() {
        var expr = or();

        if (match(TokenType.EQUAL)) {
            var equals = previous();
            var value = assignment();

            if (expr instanceof Variable) {
                var name = ((Variable) expr).name();
                return new Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private List<Stmt> block() {
        var statements = new ArrayList<Stmt>();

        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Expr comparison() {
        var expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            var operator = previous();
            var right = term();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private Stmt declaration() {
        try {
            if (match(TokenType.VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Expr equality() {
        var expr = comparison();

        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            var operator = previous();
            var right = comparison();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt expressionStatement() {
        var expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Expression(expr);
    }

    private Expr factor() {
        var expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            var operator = previous();
            var right = unary();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Stmt forStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

        Stmt initializer;
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

        Expr increment = null;
        if (!check(TokenType.RIGHT_PAREN)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

        var body = statement();

        if (increment != null) {
            body = new Block(Arrays.asList(body, new Expression(increment)));
        }

        if (condition == null) {
            condition = new Literal(true);
        }
        body = new While(condition, body);

        if (initializer != null) {
            body = new Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
        var condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

        var thenBranch = statement();
        Stmt elseBranch = null;
        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private boolean match(TokenType... types) {
        for (var type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Expr or() {
        var expr = and();

        while (match(TokenType.OR)) {
            var operator = previous();
            var right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) return new Literal(false);
        if (match(TokenType.TRUE)) return new Literal(true);
        if (match(TokenType.NIL)) return new Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Literal(previous().literal());
        }

        if (match(TokenType.IDENTIFIER)) {
            return new Variable(previous());
        }

        if (match(TokenType.LEFT_PAREN)) {
            var expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private Stmt printStatement() {
        var value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Print(value);
    }

    private Stmt statement() {
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.LEFT_BRACE)) return new Block(block());

        return expressionStatement();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type() == TokenType.SEMICOLON) return;

            switch (peek().type()) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    private Expr term() {
        var expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            var operator = previous();
            var right = factor();
            expr = new Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            var operator = previous();
            var right = unary();
            return new Unary(operator, right);
        }

        return primary();
    }

    private Stmt varDeclaration() {
        var name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(TokenType.EQUAL)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Var(name, initializer);
    }

    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
        var condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        var body = statement();

        return new While(condition, body);
    }
}
