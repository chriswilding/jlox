package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.Token;

import java.util.List;

public record Function(Token name, List<Token> params, List<Stmt> body) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFunctionStmt(this);
    }
}
