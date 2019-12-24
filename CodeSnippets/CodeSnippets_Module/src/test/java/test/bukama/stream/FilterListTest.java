package test.bukama.stream;


import org.junit.jupiter.api.Test;
import test.bukama.dto.MyElement;

import java.util.*;


import static org.assertj.core.api.Assertions.assertThat;

public class FilterListTest {

    @Test
    void testFilterCollection() {
        MyElement element1 = new MyElement("1");
        MyElement element2 = new MyElement("2");
        MyElement element3 = new MyElement("3");
        MyElement element4 = new MyElement("4");

        List<MyElement> expectedElements = new ArrayList<MyElement>();
        expectedElements.add(element2);
        expectedElements.add(element4);

        List<MyElement> allElements = new ArrayList<MyElement>();
        allElements.add(element1);
        allElements.add(element2);
        allElements.add(element3);
        allElements.add(element4);

        Set<String> uuidsToRemove = new HashSet<String>();
        uuidsToRemove.add("1");
        uuidsToRemove.add("3");

        List<MyElement> result = FilterList.filterElementsCollection(allElements, uuidsToRemove);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsAll(expectedElements)).isEqualTo(true);

    }

    @Test
    void testFilterStream() {
        MyElement element1 = new MyElement("1");
        MyElement element2 = new MyElement("2");
        MyElement element3 = new MyElement("3");
        MyElement element4 = new MyElement("4");

        List<MyElement> expectedElements = new ArrayList<MyElement>();
        expectedElements.add(element2);
        expectedElements.add(element4);

        List<MyElement> allElements = new ArrayList<MyElement>();
        allElements.add(element1);
        allElements.add(element2);
        allElements.add(element3);
        allElements.add(element4);

        Set<String> uuidsToRemove = new HashSet<String>();
        uuidsToRemove.add("1");
        uuidsToRemove.add("3");

        List<MyElement> result = FilterList.filterElementsStream(allElements, uuidsToRemove);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsAll(expectedElements)).isEqualTo(true);

    }

    @Test
    void testFilterLoop() {

        MyElement element1 = new MyElement("1");
        MyElement element2 = new MyElement("2");
        MyElement element3 = new MyElement("3");
        MyElement element4 = new MyElement("4");

        List<MyElement> expectedElements = new ArrayList<MyElement>();
        expectedElements.add(element2);
        expectedElements.add(element4);

        List<MyElement> allElements = new ArrayList<MyElement>();
        allElements.add(element1);
        allElements.add(element2);
        allElements.add(element3);
        allElements.add(element4);

        Set<String> uuidsToRemove = new HashSet<String>();
        uuidsToRemove.add("1");
        uuidsToRemove.add("3");

        List<MyElement> result = FilterList.filterElementsLoop(allElements, uuidsToRemove);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsAll(expectedElements)).isEqualTo(true);

    }
}
