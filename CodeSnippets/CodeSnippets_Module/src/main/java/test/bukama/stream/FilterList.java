package test.bukama.stream;

import test.bukama.dto.MyElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterList {

    public static List<MyElement> filterElementsCollection(List<MyElement> allElements, Set<String> uuidsToRemove) {

        allElements.removeIf(e -> uuidsToRemove.contains(e.getUuid()));

        return allElements;
    }

    public static List<MyElement> filterElementsStream(List<MyElement> allElements, Set<String> uuidsToRemove) {
        return allElements.stream().filter(e -> !uuidsToRemove.contains(e.getUuid())).collect(Collectors.toList());
    }

    public static List<MyElement> filterElementsLoop(List<MyElement> allElements, Set<String> uuidsToRemove) {
        int newSize = allElements.size() - uuidsToRemove.size();
        List<MyElement> fiteredElements = new ArrayList<MyElement>(newSize);

        for(MyElement element : allElements) {
            if(!uuidsToRemove.contains(element.getUuid())) {
                fiteredElements.add(element);
            }
        }
        return fiteredElements;
    }

}
