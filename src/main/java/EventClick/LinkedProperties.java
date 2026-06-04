package EventClick;

import java.util.*;

public class LinkedProperties extends Properties {
    private final HashSet<Object> keys = new LinkedHashSet<>();

    @Override
    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }

    @Override
    public Enumeration<Object> keys() {
        return Collections.enumeration(keys);
    }

    @Override
    public Set<Object> keySet() {
        return keys;
    }

    @Override
    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<>();
        for (Object key : keys) {
            set.add((String) key);
        }
        return set;
    }
}