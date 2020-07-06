package ru.ancevt.util.repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import ru.ancevt.util.args.Args;
import ru.ancevt.util.texttable.TextTable;

public class ReplInterpreter {

    public static void main(String[] args) {

        final Args a = ReplInterpreter.scanArgs("prompt> ");
        System.out.println(a.getString(1));

    }

    private String prompt;
    private final List<ReplCommand> commands;
    private InputStream inputStream;
    private PrintStream printStream;
    private boolean running;

    public ReplInterpreter() {
        this(null);
    }

    public ReplInterpreter(final String prompt) {
        this.prompt = prompt;
        commands = new ArrayList<>();
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public final void addCommand(final ReplCommand command) {
        commands.add(command);
    }

    public final void removeCommand(final ReplCommand command) {
        commands.remove(command);
        Log.repl.info("Remove command " + command.toString());
    }

    public final void addCommand(final String commandWord, final ReplFunction replFunction) {
        addCommand(commandWord, null, replFunction);
    }

    public final void addCommand(final String commandWord, final String commandDescription,
        final ReplFunction replFunction) {
        final ReplCommand replCommand = new ReplCommand(commandWord, commandDescription, replFunction);
        commands.add(replCommand);
        Log.repl.info("add command " + replCommand.toString());
    }

    public final void removeCommand(final String commandWord) {
        for (final ReplCommand command : commands) {
            if (command.getCommandWord().equals(commandWord)) {
                removeCommand(command);
                break;
            }
        }
    }

    public final void stop() throws IOException {
        if (inputStream != System.in) {
            inputStream.close();
        }
        if (printStream != System.out) {
            printStream.close();
        }

        Log.repl.info("Stop");

        running = false;
    }

    public final void print(Object o) {
        getPrintStream().print(o);
        Log.repl.info("Print: " + o);
    }

    public final void println(Object o) {
        getPrintStream().println(o);
        Log.repl.info("Print: " + o);
    }

    public final void start() {
        start(System.in, System.out);
    }

    public final void start(InputStream inputStream, PrintStream printStream) {
        this.printStream = printStream;

        Log.repl.info(
            "Start REPL inputStream: "
            + inputStream.toString()
            + ", printStream: "
            + printStream
        );

        running = true;

        try {
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.inputStream = inputStream)
            );

            while (running) {

                if (prompt != null) {
                    print(prompt);
                }

                final String commandLine = bufferedReader.readLine();
                if (commandLine == null) {
                    break;
                }
                if (commandLine.trim().length() == 0) {
                    continue;
                }

                execute(commandLine);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public PrintStream getPrintStream() {
        int counter = 0;
        while (printStream == null && counter < 1000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            counter++;
        }
        return printStream;
    }

    public void execute(final String commandLine) {
        
        Log.repl.info("Execute: " + commandLine);
        
        final String splitted[] = commandLine.split("\\s+");
        final String commandWord = splitted[0];

        final ReplCommand command = searchCommand(commandWord);
        if (command == null) {
            println("Unknown command: " + commandWord);
        } else {

            final String argsString = commandLine.replaceAll(commandWord, "");
            final Args args = new Args(argsString);

            try {
                command.getCommandFunction().call(args);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private ReplCommand searchCommand(final String commandWord) {
        for (final ReplCommand command : commands) {
            if (command.getCommandWord().equals(commandWord)) {
                return command;
            }
        }
        return null;
    }

    public final String formattedCommandListTable() {
        final TextTable textTable = new TextTable();
        textTable.setColumnNames(new String[]{"command", "description"});
        commands.stream().forEach((command) -> {
            textTable.addRow(new String[]{command.getCommandWord(), command.getCommandDescription()});
        });
        return textTable.render();
    }

    public final static String scanString(final InputStream inputStream, final String prompt) {
        final Args args = scanArgs(inputStream, prompt);
        return args.toString();
    }

    public final static String scanString(final String prompt) {
        return scanString(System.in, prompt);
    }

    public final static String scanString(final InputStream inputStream) {
        return scanString(inputStream, null);
    }

    public final static String scanString() {
        return scanString(System.in);
    }

    public final static Args scanArgs() {
        return scanArgs(System.in, null);
    }

    public final static Args scanArgs(final String propmt) {
        return scanArgs(System.in, propmt);
    }

    public final static Args scanArgs(final InputStream inputStream) {
        return scanArgs(inputStream, null);
    }

    public final static Args scanArgs(final InputStream inputStream, final String prompt) {

        if (prompt != null) {
            System.out.print(prompt);
        }

        try {
            final String lint;
            try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream)
            )) {
                lint = bufferedReader.readLine();
            }

            return new Args(lint);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
