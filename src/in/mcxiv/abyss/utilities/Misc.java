package in.mcxiv.abyss.utilities;

import java.io.*;

public final class Misc {
    private Misc() {
    }

    public static String getCallerMeta(int extra) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return "> %s %s called from %s %s)".formatted(
                stackTrace[2 + extra].getClassName(),
                stackTrace[2 + extra].getMethodName(),
                stackTrace[3 + extra].getClassName(),
                stackTrace[3 + extra].getMethodName()
        );
    }

    public static Object clone(Object object) {
        try {
            var bos = new ByteArrayOutputStream();
            var oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.close();

            var bis = new ByteArrayInputStream(bos.toByteArray());
            var ois = new ObjectInputStream(bis);
            Object clone = ois.readObject();
            ois.close();

            return clone;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
