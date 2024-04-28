package in.mcxiv.abyss.interfaces;

public interface CopyCloneable<T extends CopyCloneable> {
    <R extends T> void copyTo(R target);

//    default void copyFrom(T target) {
//        target.copyTo(this);
//    }
}
