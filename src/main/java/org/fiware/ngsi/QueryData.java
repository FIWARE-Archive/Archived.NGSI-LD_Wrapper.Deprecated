package org.fiware.ngsi;

import java.util.ArrayList;
import java.util.List;

public class QueryData {
    public String queryExpression;

    public List<String> entityIds;

    public List<String> types;

    public QueryData() {
        types = new ArrayList<String>();
        entityIds = new ArrayList<String>();
        queryExpression = new String();
    }
}
