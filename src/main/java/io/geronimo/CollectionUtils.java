package io.geronimo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static Collection subtract(Collection firstCollection, Collection secondCollection) {
        Collection collection = new ArrayList<>();

        for (Object object : firstCollection) {
            if (!secondCollection.contains(object)) {
                collection.add(object);
            }
        }

        return collection;
    }
}