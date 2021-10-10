package uk.co.chriswilding.lox;

import uk.co.chriswilding.lox.expr.Assign;
import uk.co.chriswilding.lox.expr.Binary;
import uk.co.chriswilding.lox.expr.Expr;
import uk.co.chriswilding.lox.expr.Grouping;
import uk.co.chriswilding.lox.expr.Literal;
import uk.co.chriswilding.lox.expr.Unary;
import uk.co.chriswilding.lox.expr.Variable;
import uk.co.chriswilding.lox.stmt.Block;
import uk.co.chriswilding.lox.stmt.Expression;
import uk.co.chriswilding.lox.stmt.Print;
import uk.co.chriswilding.lox.stmt.Stmt;
import uk.co.chriswilding.lox.stmt.Var;

import java.util.List;

class Interpreter implements uk.co.chriswilding.lox.expr.Visitor<Object>, uk.co.chriswilding.lox.stmt.Visitor<Void> {
    private Environment environment = new Environment();

    @Override
    public Object visitAssignExpr(Assign expr) {
        var value = evaluate(expr.value());
        environment.assign(expr.name(), value);
        return value;
    }

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
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements(), new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression());
        return null;
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
    public Void visitPrintStmt(Print stmt) {
        var value = evaluate(stmt.expression());
        System.out.println(stringify(value));
        return null;
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

    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name());
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;
        if (stmt.initializer() != null) {
            value = evaluate(stmt.initializer());
        }

        environment.define(stmt.name().lexeme(), value);
        return null;
    }

    void interpret(List<Stmt> statements) {
        try {
            for (var statement : statements) {
                execute(statement);
            }
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

    private void execute(Stmt stmt) {
        stmt.accept(this);
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

    private void executeBlock(List<Stmt> statements, Environment environment) {
        var previous = this.environment;
        try {
            this.environment = environment;

            for (var statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
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
