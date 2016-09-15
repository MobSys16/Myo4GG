package de.uni_luebeck.imis.gestures.myo.CustomGestures;

/**
 * Created by finnfahrenkrug on 14.09.16.
 */
import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }
}
