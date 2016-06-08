package interdroid.swancore.swansong;

/**
 * An interface marking an object as convertible to and from a string form.
 * <p/>
 * Implementing classes must ensure that all object state is persisted in
 * the string version of the object.
 * <p/>
 * Implementing classes must also include a static method:
 * <p/>
 * public E parse(String parseable);
 *
 * @param <E> the type implementing the interface
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public interface Parseable<E> {

    /**
     * The return value of this must reconstruct the object
     * when passed to parse(String).
     *
     * @return the parseable string form of the object.
     */
    String toParseString();
}
