package uk.co.chriswilding.lox.expr;

public interface Visitor<R> {
    R visitAssignExpr(Assign expr);

    R visitBinaryExpr(Binary expr);

    R visitGroupingExpr(Grouping expr);

    R visitLiteralExpr(Literal expr);

    R visitLogicalExpr(Logical logical);

    R visitUnaryExpr(Unary expr);

    R visitVariableExpr(Variable expr);
}
