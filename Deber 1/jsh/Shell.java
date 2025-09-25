package jsh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class Shell {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private final State state = new State();

    public void run() throws IOException {
        while (true) {
            System.out.print("jsh>> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String expanded = expandHistory(line);
            if (expanded == null) {
                continue;
            }

            state.getHistory().addRaw(expanded);

            if ("exit".equals(expanded)) {
                break;
            }

            try {
                Parser.Line parsed = Parser.parse(expanded);
                if (parsed.sequence.size() == 1) {
                    executeForeground(parsed.sequence.get(0));
                } else {
                    executeBackgroundSequence(parsed.sequence);
                }
            } catch (Parser.ParseException e) {
                System.out.println(expanded + ":");
                System.err.println(e.getMessage());
            }
        }
    }

    private String expandHistory(String line) {
        if (line.startsWith("!#")) {
            String last = state.getHistory().getMostRecent();
            if (last == null) {
                System.out.println(line + ":");
                System.err.println("history is empty");
                return null;
            }
            return last;
        }
        if (line.startsWith("!")) {
            try {
                long n = Long.parseLong(line.substring(1));
                String cmd = state.getHistory().getByNumber(n);
                if (cmd == null) {
                    System.out.println(line + ":");
                    System.err.println("no such entry in history");
                    return null;
                }
                return cmd;
            } catch (NumberFormatException ex) {
            }
        }
        return line;
    }

    private void executeForeground(Parser.Command cmd) {
        switch (cmd.program) {
            case "cd":
                printHeader(cmd);
                handleCd(cmd);
                return;
            case "history":
                printHeader(cmd);
                for (String entry : state.getHistory().listWithNumbers()) {
                    System.out.println(entry);
                }
                return;
            default:
                // external
        }

        runExternal(cmd, false, 0);
    }

    private void handleCd(Parser.Command cmd) {
        Path target;
        if (cmd.args.isEmpty()) {
            target = Path.of(System.getProperty("user.home"));
        } else {
            target = state.getCurrentDir().resolve(cmd.args.get(0)).normalize();
        }
        if (!Files.exists(target) || !Files.isDirectory(target)) {
            System.err.println("cd: no such directory: " + target);
            return;
        }
        state.setCurrentDir(target);
    }

    private void executeBackgroundSequence(List<Parser.Command> sequence) {
        for (Parser.Command c : sequence) {
            if ("exit".equals(c.program)) {
                continue;
            }
            int jobId = state.nextJobId();
            switch (c.program) {
                case "cd":
                    printHeader(c);
                    handleCd(c);
                    System.out.println("[" + jobId + "] -1");
                    break;
                case "history":
                    printHeader(c);
                    for (String entry : state.getHistory().listWithNumbers()) {
                        System.out.println(entry);
                    }
                    System.out.println("[" + jobId + "] -1");
                    break;
                default:
                    long pid = runExternal(c, true, jobId);
                    System.out.println("[" + jobId + "] " + pid);
            }
        }
    }

    private void printHeader(Parser.Command cmd) {
        System.out.println(cmd + ":");
    }

    private long runExternal(Parser.Command cmd, boolean background, int jobId) {
        printHeader(cmd);
        List<String> tokens = new ArrayList<>(cmd.toCommandLineTokens());
        ProcessBuilder pb = new ProcessBuilder(tokens);
        pb.directory(state.getCurrentDir().toFile());
        if (cmd.inputFile != null) {
            File in = state.getCurrentDir().resolve(cmd.inputFile).toFile();
            if (!in.exists()) {
                System.err.println("input file not found: " + in.getPath());
                return -1;
            }
            pb.redirectInput(in);
        }
        try {
            Process p = pb.start();
            long pid = p.pid();
            if (background) {
                StreamGobbler.pipeAsync(p.getInputStream(), System.out);
                StreamGobbler.pipeAsync(p.getErrorStream(), System.err);
                return pid;
            } else {
                BytesCollector outC = BytesCollector.start(p.getInputStream());
                BytesCollector errC = BytesCollector.start(p.getErrorStream());
                int code = p.waitFor();
                byte[] out = outC.getBytes();
                byte[] err = errC.getBytes();
                if (code == 0) {
                    if (out.length > 0) SynchronizedPrinter.write(System.out, out, 0, out.length);
                } else {
                    if (err.length > 0) SynchronizedPrinter.write(System.err, err, 0, err.length);
                }
                return pid;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return -1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("interrupted");
            return -1;
        }
    }
}
