package uk.co.chriswilding.lox.expr;

public record Literal(Object value) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }
}
