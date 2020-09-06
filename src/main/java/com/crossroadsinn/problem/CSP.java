package com.crossroadsinn.problem;

import java.util.ArrayList;

public interface CSP {

    public ArrayList<CSP> getChildren();

    public int heuristic();

    public boolean isSolution();
}
