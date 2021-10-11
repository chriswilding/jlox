package uk.co.chriswilding.lox.stmt;

import uk.co.chriswilding.lox.Token;
import uk.co.chriswilding.lox.expr.Variable;

import java.util.List;

public record Class(Token name, Variable superclass, List<Function> methods) implements Stmt {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitClassStmt(this);
    }
}
