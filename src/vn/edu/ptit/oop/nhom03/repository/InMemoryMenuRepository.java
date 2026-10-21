package vn.edu.ptit.oop.nhom03.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.ptit.oop.nhom03.model.*;

/** Menu lưu trong bộ nhớ, có sẵn dữ liệu mẫu tiếng Việt. */
public class InMemoryMenuRepository implements MenuRepository {

    private final List<MenuItem> items = new ArrayList<>();

    public InMemoryMenuRepository() {
        // Khởi tạo đồ uống với DrinkSize.S để giá truyền vào đúng là giá gốc (Size S)
        Drink caPheSua = new Drink("D02", "Cà phê sữa", 25000, DrinkSize.S);
        Drink traDao   = new Drink("D04", "Trà đào", 35000, DrinkSize.S);

        items.add(new Drink("D01", "Cà phê đen", 20000, DrinkSize.S));
        items.add(caPheSua);
        items.add(new Drink("D03", "Bạc xỉu", 28000, DrinkSize.S));
        items.add(traDao);
        items.add(new Drink("D05", "Trà sữa trân châu", 38000, DrinkSize.S));
        items.add(new Drink("D06", "Trà vải", 35000, DrinkSize.S));

        // >= 3 đồ ăn
        Food banhMi = new Food("F01", "Bánh mì", 25000);
        Food flan   = new Food("F03", "Bánh flan", 15000);
        items.add(banhMi);
        items.add(new Food("F02", "Bánh sandwich", 20000));
        items.add(flan);

        // >= 2 combo
        Combo sang = new Combo("C01", "Combo buổi sáng");
        sang.addItem(caPheSua);
        sang.addItem(banhMi);
        Combo chieu = new Combo("C02", "Combo chiều mát");
        chieu.addItem(traDao);
        chieu.addItem(flan);
        items.add(sang);
        items.add(chieu);
    }

    @Override
    public List<MenuItem> findAll() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public List<MenuItem> findByCategory(Category category) {
        List<MenuItem> result = new ArrayList<>();
        for (MenuItem m : items) {
            if (inCategory(m, category)) result.add(m);
        }
        return result;
    }

    private static boolean inCategory(MenuItem m, Category c) {
        return c.getLabel().equals(m.getCategory());
    }
}
