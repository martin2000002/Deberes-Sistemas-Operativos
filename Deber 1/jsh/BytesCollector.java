package jsh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class BytesCollector implements Runnable {
    private final InputStream in;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(8192);

    private BytesCollector(InputStream in) { this.in = in; }

    static BytesCollector start(InputStream in) {
        BytesCollector c = new BytesCollector(in);
        Thread t = new Thread(c, "collector");
        t.setDaemon(true);
        t.start();
        return c;
    }

    @Override
    public void run() {
        byte[] buf = new byte[8192];
        int n;
        try {
            while ((n = in.read(buf)) != -1) {
                buffer.write(buf, 0, n);
            }
        } catch (IOException ignored) {}
    }

    byte[] getBytes() { return buffer.toByteArray(); }
}
