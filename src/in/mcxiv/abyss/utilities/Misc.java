package in.mcxiv.abyss.utilities;

public final class Misc {
    private Misc() {
    }

    public static String getCallerMeta(int extra) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return "> %s %s called from %s %s, %s %s, %s %s ".formatted(
                stackTrace[2 + extra].getClassName(),
                stackTrace[2 + extra].getMethodName(),
                stackTrace[3 + extra].getClassName(),
                stackTrace[3 + extra].getMethodName(),
                stackTrace[4 + extra].getClassName(),
                stackTrace[4 + extra].getMethodName(),
                stackTrace[5 + extra].getClassName(),
                stackTrace[5 + extra].getMethodName()
        );
    }

}
