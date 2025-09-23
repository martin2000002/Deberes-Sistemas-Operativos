package jsh;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Bounded FIFO history buffer with numbered listing.
 */
class History {
    private final int capacity;
    private final Deque<String> buffer = new ArrayDeque<>();
    private long startIndex = 1; // absolute number of the oldest entry

    History(int capacity) {
        this.capacity = capacity;
    }

    void addRaw(String line) {
        if (buffer.size() == capacity) {
            buffer.removeFirst();
            startIndex++;
        }
        buffer.addLast(line);
    }

    List<String> listWithNumbers() {
        List<String> out = new ArrayList<>();
        long n = startIndex;
        for (String s : buffer) {
            out.add(n + " " + s);
            n++;
        }
        return out;
    }

    String getByNumber(long n) {
        long idx = n - startIndex;
        if (idx < 0 || idx >= buffer.size()) return null;
        int i = 0;
        for (String s : buffer) {
            if (i == idx) return s;
            i++;
        }
        return null;
    }

    String getMostRecent() {
        return buffer.peekLast();
    }
}
