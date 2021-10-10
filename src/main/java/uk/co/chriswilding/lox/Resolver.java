package uk.co.chriswilding.lox;

import lombok.RequiredArgsConstructor;
import uk.co.chriswilding.lox.expr.Assign;
import uk.co.chriswilding.lox.expr.Binary;
import uk.co.chriswilding.lox.expr.Call;
import uk.co.chriswilding.lox.expr.Expr;
import uk.co.chriswilding.lox.expr.Grouping;
import uk.co.chriswilding.lox.expr.Literal;
import uk.co.chriswilding.lox.expr.Logical;
import uk.co.chriswilding.lox.expr.Unary;
import uk.co.chriswilding.lox.expr.Variable;
import uk.co.chriswilding.lox.stmt.Block;
import uk.co.chriswilding.lox.stmt.Expression;
import uk.co.chriswilding.lox.stmt.Function;
import uk.co.chriswilding.lox.stmt.If;
import uk.co.chriswilding.lox.stmt.Print;
import uk.co.chriswilding.lox.stmt.Return;
import uk.co.chriswilding.lox.stmt.Stmt;
import uk.co.chriswilding.lox.stmt.Var;
import uk.co.chriswilding.lox.stmt.While;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@RequiredArgsConstructor
class Resolver implements uk.co.chriswilding.lox.expr.Visitor<Void>, uk.co.chriswilding.lox.stmt.Visitor<Void> {
    private enum FunctionType {
        NONE,
        FUNCTION
    }

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;

    @Override
    public Void visitAssignExpr(Assign expr) {
        resolve(expr.value());
        resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visitBinaryExpr(Binary expr) {
        resolve(expr.left());
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        beginScope();
        resolve(stmt.statements());
        endScope();
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        resolve(expr.callee());
        expr.arguments().forEach(this::resolve);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        resolve(stmt.expression());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        declare(stmt.name());
        define(stmt.name());

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Grouping expr) {
        resolve(expr.expression());
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        resolve(stmt.condition());
        resolve(stmt.thenBranch());
        if (stmt.elseBranch() != null) resolve(stmt.elseBranch());
        return null;
    }

    @Override
    public Void visitLiteralExpr(Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Logical expr) {
        resolve(expr.left());
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        resolve(stmt.expression());
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword(), "Can't return from top-level code.");
        }

        if (stmt.value() != null) {
            resolve(stmt.value());
        }
        return null;
    }

    @Override
    public Void visitUnaryExpr(Unary expr) {
        resolve(expr.right());
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        declare(stmt.name());
        if (stmt.initializer() != null) {
            resolve(stmt.initializer());
        }
        define(stmt.name());
        return null;
    }

    @Override
    public Void visitVariableExpr(Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name().lexeme()) == Boolean.FALSE) {
            Lox.error(expr.name(), "Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        resolve(stmt.condition());
        resolve(stmt.body());
        return null;
    }

    void resolve(Expr expr) {
        expr.accept(this);
    }

    void resolve(List<Stmt> statements) {
        statements.forEach(this::resolve);
    }

    void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme(), true);
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        var scope = scopes.peek();
        if (scope.containsKey(name.lexeme())) {
            Lox.error(name, "Already a variable with this name in this scope.");
        }
        scope.put(name.lexeme(), false);
    }

    private void endScope() {
        scopes.pop();
    }

    private void resolveFunction(Function function, FunctionType type) {
        var enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        function.params().forEach(param -> {
            declare(param);
            define(param);
        });
        resolve(function.body());
        endScope();
        currentFunction = enclosingFunction;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (var i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }
}
