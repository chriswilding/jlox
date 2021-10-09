package uk.co.chriswilding.lox.expr;

public interface Expr {
    <R> R accept(Visitor<R> visitor);
}
