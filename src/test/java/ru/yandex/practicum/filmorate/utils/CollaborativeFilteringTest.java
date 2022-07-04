package ru.yandex.practicum.filmorate.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.utils.CollaborativeFiltering.recommendItems;

class CollaborativeFilteringTest {

    @Test
    void test1_recommendItems() {
        //Given
        List<Integer> userItems1 = List.of(1,2,3,4);
        List<Integer> userItems2 = List.of(2,3);
        List<Integer> userItems3 = List.of(1,5,3,10);
        Map<Integer, List<Integer>> data = Map.of(
                1, userItems1,
                2, userItems2,
                3, userItems3
        );

        //When
        List<Integer> recommendedItems1 = recommendItems(data, 2);
        List<Integer> recommendedItems2 = recommendItems(data, 3);


        //Then
        List<Integer> expectedList1 = List.of(1,4);
        List<Integer> expectedList2 = List.of(2,4);

        assertEquals(expectedList1, recommendedItems1,
                "incorrect list of recommendations case1");
        assertEquals(expectedList2, recommendedItems2,
                "incorrect list of recommendations case2");
    }

}