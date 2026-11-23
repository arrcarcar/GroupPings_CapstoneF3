package Swing.Accounts;

import Swing.FileHandling.FileHelper;
import Swing.Foods.FoodAbstractFactory;
import Swing.Foods.FoodItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Supplier extends Account {
    public Supplier(String username, String password) {
        super(username, password);
    }
    public void addItem(FoodItem food) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("ItemList.txt"))){
            String line = food.getName() + "," + this.getUsername();
            bw.write(line);
            bw.newLine();
        }
    }

    public ArrayList<FoodItem> getListedFoods() throws IOException{
        ArrayList<FoodItem> list = new ArrayList<FoodItem>();
        try(BufferedReader br = new BufferedReader(new FileReader("ItemList.txt"))){
            String line;
            // ID, FOODNAME, QTY, TYPE, HRS LEFT, DONOR
            while((line = br.readLine()) != null){
                String[] s = line.split(",");
                if(s[5].equals(this.getUsername())){
                    list.add(FoodAbstractFactory.instantiate(s));
                }
            }
        }
        System.out.println(list); // for testing purposes
        return list;
    }
    public void unlistFoodItem(FoodItem food) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("ItemList.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] read = line.split(",");
                if (read.length >= 2
                        && read[0].equalsIgnoreCase(food.getBatchId())
                        && read[1].equalsIgnoreCase(food.getName())) {
                    FileHelper.removeFromFile(line,"ItemList.txt" );
                }
            }
        }
    }
}
