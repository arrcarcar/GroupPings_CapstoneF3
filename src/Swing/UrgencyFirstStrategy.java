package Swing;

import Swing.Foods.FoodItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UrgencyFirstStrategy implements DispatchStrategy {

    @Override
    public List<FoodItem> sortAndMatch(List<FoodItem> items) {
        List<FoodItem> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparingInt(FoodItem::getHoursLeft));
        return sorted;
    }
}
