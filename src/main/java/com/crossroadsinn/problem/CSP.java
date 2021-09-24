package com.crossroadsinn.problem;

import java.util.List;

public interface CSP {

    public List<CSP> getChildren();

    public int heuristic();

    public boolean isSolution();
}
