/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.SearchQueryState;
import ru.byazen.misc.TextInputModel;

public final class SearchQueryTextAdapter
implements TextInputModel {
    private final SearchQueryState searchQueryState;

    public SearchQueryTextAdapter(SearchQueryState searchQueryState) {
        this.searchQueryState = searchQueryState;
    }

    @Override
    public int getMaximumLength() {
        return 32;
    }

    @Override
    public String getText() {
        return this.searchQueryState.getQuery();
    }

    @Override
    public void setText(String text) {
        this.searchQueryState.setQuery(text == null ? "" : text);
    }
}

