package uk.co.chriswilding.lox.expr;

import uk.co.chriswilding.lox.Token;

public record Assign(Token name, Expr value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignExpr(this);
    }
}
