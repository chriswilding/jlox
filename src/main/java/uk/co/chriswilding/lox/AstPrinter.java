package uk.co.chriswilding.lox;

import uk.co.chriswilding.lox.expr.*;

class AstPrinter implements Visitor<String> {
    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value() == null) return "nil";
        return expr.value().toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expr... exprs) {
        var builder = new StringBuilder();

        builder.append('(').append(name);
        for (var expr : exprs) {
            builder.append(' ');
            builder.append(expr.accept(this));
        }
        builder.append(')');

        return builder.toString();
    }
}
