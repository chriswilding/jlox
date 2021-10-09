package uk.co.chriswilding.lox.expr;

import uk.co.chriswilding.lox.Token;

public record Unary(Token operator, Expr right) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }
}
