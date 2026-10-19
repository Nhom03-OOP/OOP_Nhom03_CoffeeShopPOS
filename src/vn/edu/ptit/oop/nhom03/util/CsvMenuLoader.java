package vn.edu.ptit.oop.nhom03.util;

import vn.edu.ptit.oop.nhom03.model.MenuItem;
import vn.edu.ptit.oop.nhom03.model.Drink;
import vn.edu.ptit.oop.nhom03.model.Food;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvMenuLoader {


    public static List<MenuItem> loadMenuData(String filePath) {
        List<MenuItem> menuList = new ArrayList<>();
        int idCounter = 1;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3) {
                    String name = data[0].trim();
                    int price = Integer.parseInt(data[1].trim());
                    String type = data[2].trim();

                    String id = "M" + String.format("%03d", idCounter++);


                    MenuItem item = null;
                    if (type.equalsIgnoreCase("Drink")) {
                        item = new Drink(id, name, price);
                    } else if (type.equalsIgnoreCase("Food")) {
                        item = new Food(id, name, price);
                    }

                    if (item != null) {
                        menuList.add(item);
                    }
                }
            }
            System.out.println("Đọc file CSV thành công! Tổng số món: " + menuList.size());
        } catch (IOException | NumberFormatException e) {
            System.out.println("Lỗi khi đọc file CSV: " + e.getMessage());
        }
        return menuList;
    }

    public static void main(String[] args) {
        String testFile = "menu_test.csv";

        try (FileWriter fw = new FileWriter(testFile)) {
            fw.write("Cafe Đen,15000,Drink\n");
            fw.write("Cafe Sữa,20000,Drink\n");
            fw.write("Bánh Mì,15000,Food\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<MenuItem> data = loadMenuData(testFile);
        for (MenuItem item : data) {
            System.out.println("ID: " + item.getId() + " | Tên món: " + item.getName() +
                    " | Giá: " + item.getPrice() + " | Loại: " + item.getCategory());
        }
    }
}