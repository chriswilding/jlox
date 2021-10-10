package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.expr.Expr;

public record If(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitIfStmt(this);
    }
}
