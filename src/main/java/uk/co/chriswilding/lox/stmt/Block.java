package uk.co.chriswilding.lox.stmt;

import java.util.List;

public record Block(List<Stmt> statements) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitBlockStmt(this);
    }
}
