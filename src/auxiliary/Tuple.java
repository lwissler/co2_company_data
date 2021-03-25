package auxiliary;

public class Tuple<S, T> extends Object {
    public S key;
    public T value;

    public Tuple(S key, T value) { 
        this.key = key;
        this.value = value;
    }
}