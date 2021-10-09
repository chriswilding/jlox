package uk.co.chriswilding.lox.expr;

public record Grouping(Expr expression) implements Expr {
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitGroupingExpr(this);
    }
}
