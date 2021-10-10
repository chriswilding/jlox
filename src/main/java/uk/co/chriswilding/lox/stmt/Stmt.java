package uk.co.chriswilding.lox.stmt;

public interface Stmt {
    <R> R accept(Visitor<R> visitor);
}
