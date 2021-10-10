package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.expr.Expr;

public record While(Expr condition, Stmt body) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitWhileStmt(this);
    }
}
