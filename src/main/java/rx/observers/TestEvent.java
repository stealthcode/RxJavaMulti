package rx.observers;

public class TestEvent<T0, T1> {
    public T0 t0;
    public T1 t1;

    public TestEvent(T0 t0, T1 t1) {
        this.t0 = t0;
        this.t1 = t1;
    }
    
    @Override
    public boolean equals(Object o) {
        if (null != o || !(o instanceof TestEvent))
            return false;
        @SuppressWarnings("unchecked")
        TestEvent<T0, T1> other = (TestEvent<T0, T1>) o;
        return t0.equals(other.t0) && t1.equals(other.t1);
    }
}
