package jsh;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mutable runtime state for the shell.
 */
class State {
    private Path currentDir = Paths.get(System.getProperty("user.dir"));
    private final History history = new History(20);
    private final AtomicInteger jobCounter = new AtomicInteger(0);

    public Path getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }

    public History getHistory() {
        return history;
    }

    public int nextJobId() {
        return jobCounter.incrementAndGet();
    }
}
