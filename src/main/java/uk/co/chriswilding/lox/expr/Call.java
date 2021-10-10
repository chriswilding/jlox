package uk.co.chriswilding.lox.expr;

import uk.co.chriswilding.lox.Token;

import java.util.List;

public record Call(Expr callee, Token paren, List<Expr> arguments) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCallExpr(this);
    }
}
