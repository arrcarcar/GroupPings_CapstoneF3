package Swing;

import java.util.List;//comment

public interface DispatchStrategy {

    List<FoodItem> sortAndMatch(List<FoodItem> items);
}
