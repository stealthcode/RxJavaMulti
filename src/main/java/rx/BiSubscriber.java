package rx;


public abstract class BiSubscriber<T0, T1> extends DualSubscriber<T0, T1> {

    protected BiSubscriber() {
        super();
    }

    protected BiSubscriber(Subscriber<?> op) {
        this();
        this.cs.add(op);
    }
}
