package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.Token;
import uk.co.chriswilding.lox.expr.Expr;

public record Return(Token keyword, Expr value) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitReturnStmt(this);
    }
}
