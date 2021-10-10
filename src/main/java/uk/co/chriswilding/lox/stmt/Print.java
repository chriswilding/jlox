package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.expr.Expr;

public record Print(Expr expression) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitPrintStmt(this);
    }
}
