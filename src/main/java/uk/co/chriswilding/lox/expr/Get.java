package uk.co.chriswilding.lox.expr;

import uk.co.chriswilding.lox.Token;

public record Get(Expr object, Token name) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }
}
