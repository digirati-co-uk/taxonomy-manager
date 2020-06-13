package com.digirati.taxman.analysis;

public class IdWithPosition<IdT> {
    private final IdT id;
    private final int beginPosition;
    private final int endPosition;

    public IdWithPosition(IdT id, int beginPosition, int endPosition) {
        this.id = id;
        this.beginPosition = beginPosition;
        this.endPosition = endPosition;
    }

    public IdT getId() {
        return id;
    }

    public int getBeginPosition() {
        return beginPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }
}
