package uk.co.chriswilding.lox.stmt;

public interface Visitor<R> {
    R visitBlockStmt(Block block);

    R visitExpressionStmt(Expression stmt);

    R visitIfStmt(If stmt);

    R visitPrintStmt(Print stmt);

    R visitVarStmt(Var stmt);

    R visitWhileStmt(While stmt);
}
