package ru.ancevt.util.repl;

import ru.ancevt.util.string.ToStringBuilder;

public class ReplCommand {

    private final String commandWord;
    private final String commandDescription;
    private final ReplFunction commandFunction;

    public ReplCommand(final String commandWord, final String commandDescription, final ReplFunction commandFunction) {
        if (commandWord == null || commandWord.length() == 0 || !validateCommandWord(commandWord)) {
            throw new ReplRuntimeException(commandWord);
        }

        if (commandFunction == null) {
            throw new NullPointerException();
        }

        this.commandWord = commandWord;
        this.commandDescription = commandDescription;
        this.commandFunction = commandFunction;
    }

    private static final boolean validateCommandWord(final String commandWord) {
        return commandWord.matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$");
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public String getCommandWord() {
        return commandWord;
    }

    public ReplFunction getCommandFunction() {
        return commandFunction;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendAll("commandWord", "commandDescription")
            .build();
    }

}
