package me.ravriv.lite.utils;

public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A component1() {
        return first;
    }

    public B component2() {
        return second;
    }
}