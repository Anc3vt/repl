package ru.ancevt.util.repl;

import ru.ancevt.util.args.Args;

@FunctionalInterface
public interface ReplFunction {
    void call(Args args);
}
