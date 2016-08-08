package com.jshvarts.flatstanley.model;

import java.util.List;

public class FlatStanleyItems {
    public List<FlatStanley> getFlatStanleys() {
        return flatStanleys;
    }

    private List<FlatStanley> flatStanleys;

    public FlatStanleyItems(List<FlatStanley> flatStanleys) {
        this.flatStanleys = flatStanleys;
    }
}
