package io.github.rainyaphthyl.potteckit.server.phaseclock;

public class IllegalDimensionException extends IllegalStateException {
    public IllegalDimensionException(GamePhase phase, boolean dimensionRequired) {
        super(phase + " should" + (dimensionRequired ? " " : " NOT ") + "be " + " dimensional.");
    }
}
