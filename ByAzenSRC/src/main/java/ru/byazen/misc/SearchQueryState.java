/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

public final class SearchQueryState {
    private String query = "";

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }

    public boolean hasQuery() {
        return this.query != null && !this.query.isBlank();
    }
}

