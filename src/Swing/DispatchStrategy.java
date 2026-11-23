package Swing;

import java.util.List;

public interface DispatchStrategy {

    List<FoodItem> sortAndMatch(List<FoodItem> items);
}
