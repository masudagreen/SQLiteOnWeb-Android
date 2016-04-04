package io.github.skyhacker2.sqliteonweb.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eleven on 16/4/3.
 */
public class QueryResult extends Message {
    public List rows;
    public List<String> columnNames;

    public QueryResult() {
        rows = new ArrayList();
    }
}
