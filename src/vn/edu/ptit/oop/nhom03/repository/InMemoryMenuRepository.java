package vn.edu.ptit.oop.nhom03.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.ptit.oop.nhom03.model.Category;
import vn.edu.ptit.oop.nhom03.model.Combo;
import vn.edu.ptit.oop.nhom03.model.Drink;
import vn.edu.ptit.oop.nhom03.model.Food;
import vn.edu.ptit.oop.nhom03.model.MenuItem;

/** Menu lưu trong bộ nhớ, có sẵn dữ liệu mẫu tiếng Việt. */
public class InMemoryMenuRepository implements MenuRepository {

    private final List<MenuItem> items = new ArrayList<>();

    public InMemoryMenuRepository() {
        // >= 6 đồ uống
        Drink caPheSua = new Drink("D02", "Cà phê sữa", 25000, Drink.Size.M);
        Drink traDao   = new Drink("D04", "Trà đào", 35000, Drink.Size.M);
        items.add(new Drink("D01", "Cà phê đen", 20000, Drink.Size.M));
        items.add(caPheSua);
        items.add(new Drink("D03", "Bạc xỉu", 28000, Drink.Size.M));
        items.add(traDao);
        items.add(new Drink("D05", "Trà sữa trân châu", 38000, Drink.Size.M));
        items.add(new Drink("D06", "Trà vải", 35000, Drink.Size.M));

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

    // Hiện MenuItem.getCategory() trả String nên so theo nhãn.
    // Khi MenuItem đổi sang trả enum Category thì sửa thành: return m.getCategory() == c;
    private static boolean inCategory(MenuItem m, Category c) {
        return c.getLabel().equals(m.getCategory());
    }
}