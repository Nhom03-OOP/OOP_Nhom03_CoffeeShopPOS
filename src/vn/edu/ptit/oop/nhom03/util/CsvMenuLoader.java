package vn.edu.ptit.oop.nhom03.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvMenuLoader {

    public static List<String[]> loadMenuData(String filePath) {
        List<String[]> menuList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                menuList.add(data);
            }
            System.out.println("Đọc file CSV thành công! Tổng số món: " + menuList.size());
        } catch (IOException e) {
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


        List<String[]> data = loadMenuData(testFile);
        for (String[] row : data) {
            System.out.println("Tên món: " + row[0] + " | Giá: " + row[1] + " | Loại: " + row[2]);
        }
    }
}