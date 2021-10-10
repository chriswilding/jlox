package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.Token;
import uk.co.chriswilding.lox.expr.Expr;

public record Var(Token name, Expr initializer) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitVarStmt(this);
    }
}
