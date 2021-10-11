package uk.co.chriswilding.lox;

import lombok.RequiredArgsConstructor;
import uk.co.chriswilding.lox.stmt.Function;

import java.util.List;

@RequiredArgsConstructor
class LoxFunction implements LoxCallable {
    private final Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    @Override
    public int arity() {
        return declaration.params().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        var environment = new Environment(closure);

        for (var i = 0; i < declaration.params().size(); i++) {
            environment.define(declaration.params().get(i).lexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body(), environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.value();
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name().lexeme() + ">";
    }

    LoxFunction bind(LoxInstance instance) {
        var environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }
}
