package databaselib;

import java.util.ArrayList;
import java.util.Map;

public interface Callback {
    public void run(ArrayList<Map<String, Object>> result);
}
