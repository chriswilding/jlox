package uk.co.chriswilding.lox;

import uk.co.chriswilding.lox.expr.Binary;
import uk.co.chriswilding.lox.expr.Expr;
import uk.co.chriswilding.lox.expr.Grouping;
import uk.co.chriswilding.lox.expr.Literal;
import uk.co.chriswilding.lox.expr.Unary;
import uk.co.chriswilding.lox.expr.Visitor;

class Interpreter implements Visitor<Object> {
    @Override
    public Object visitBinaryExpr(Binary expr) {
        var left = evaluate(expr.left());
        var right = evaluate(expr.right());

        return switch (expr.operator().type()) {
            case MINUS -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left - (double) right;
            }
            case SLASH -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left / (double) right;

            }
            case STAR -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left * (double) right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    yield left + (String) right;
                }

                throw new RuntimeError(expr.operator(), "Operands must be two numbers or two strings.");
            }
            case GREATER -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperand(expr.operator(), left, right);
                yield (double) left <= (double) right;
            }
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            default -> null;
        };
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression());
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value();
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        var right = evaluate(expr.right());

        return switch (expr.operator().type()) {
            case BANG -> !isTruthy(right);
            case MINUS -> {
                checkNumberOperand(expr.operator(), right);
                yield -(double) right;
            }
            default -> null;
        };
    }

    void interpret(Expr expression) {
        try {
            var value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            var text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}
