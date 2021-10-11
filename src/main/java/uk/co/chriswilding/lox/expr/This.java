package uk.co.chriswilding.lox.expr;

import uk.co.chriswilding.lox.Token;

public record This(Token keyword) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitThisExpr(this);
    }
}
