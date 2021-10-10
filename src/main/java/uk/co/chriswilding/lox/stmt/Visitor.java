package uk.co.chriswilding.lox.stmt;

public interface Visitor<R> {
    R visitBlockStmt(Block block);

    R visitExpressionStmt(Expression stmt);

    R visitPrintStmt(Print stmt);

    R visitVarStmt(Var stmt);
}
