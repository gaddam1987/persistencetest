package gaddam1987.github.persistance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Naresh on 10/02/2017.
 */
public class TestSorting {
    public static void main(String[] args) throws JsonProcessingException {
        Multimap<Long, String> build = MultimapBuilder
                .treeKeys(Ordering.natural().reverse())
                .linkedListValues()
                .build();
        build.put(1L, "Naresh1");
        build.put(11L, "Naresh11");
        build.put(7L, "Naresh7");
        build.put(4L, "Naresh4");

        Map<Long, Collection<String>> longCollectionMap = build.asMap();

        System.out.println(longCollectionMap);

        for (Long aLong : longCollectionMap.keySet()) {
            System.out.println(longCollectionMap.get(aLong));
        }

        System.out.println(new ObjectMapper().writer().writeValueAsString(longCollectionMap));

    }
}
