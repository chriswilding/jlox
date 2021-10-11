package uk.co.chriswilding.lox.expr;

import uk.co.chriswilding.lox.Token;

public record Super(Token keyword, Token method) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitSuperExpr(this);
    }
}
