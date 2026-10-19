# Báo cáo tiến độ – Coffee Shop POS (Đề 5)

> Ngày kiểm tra: 18/10/2026 · Commit kiểm tra: `2ead1a2` (nhánh `main`)
> Đối chiếu với: thẻ đề Nhóm 5 trong tài liệu INT1332 và bảng phân công nội bộ (`de_tai.md`).

## 1. Tóm tắt nhanh

| Hạng mục | Trạng thái |
|---|---|
| Biên dịch (`javac` + `lib/jfreechart-1.5.4.jar`) | ✅ Biên dịch được, không lỗi (chỉ có cảnh báo deprecated do `new Locale(...)`) |
| Chạy được ứng dụng hoàn chỉnh | ❌ **Chưa.** `Main.java` vẫn in `Hello, World!`, chưa có `BaseWindow` và `MainWindow` |
| Lớp Model + Strategy (bước 1–3) | ✅ Gần đủ, còn vài lỗi nhỏ |
| Giao diện gọi món (bước 4 – phần trái) | ❌ Chưa có `MenuPanel` |
| Giỏ hàng + thanh toán (bước 4 phải, bước 5) | 🟡 Đã có `CartPanel`, `CheckoutDialog` nhưng chưa ghép, còn lỗi logic |
| Báo cáo + biểu đồ (bước 6–7) | 🟡 Đã có `ReportWindow` với 3 biểu đồ, chưa nối dữ liệu thật |
| BONUS (sơ đồ bàn + đọc CSV) | 🟡 Có `TablePanel` và `CsvMenuLoader` dạng thử nghiệm, chưa tích hợp |
| Yêu cầu giao diện chung (menu, toolbar, status bar, icon) | ❌ Chưa có vì thiếu `BaseWindow`/`MainWindow` |
| Tài liệu (README, UML, báo cáo, slide) | ❌ README vẫn là mẫu VS Code, `docs/` trống |

**Ước lượng mức hoàn thành: khoảng 50–55%.** Phần backend khá vững. Điểm nghẽn lớn nhất là **chưa có khung `BaseWindow` → `MainWindow` → `MenuPanel`** để ghép các phần đã viết thành một ứng dụng chạy được.

---

## 2. Tiến độ theo từng thành viên

| # | Thành viên | Nhiệm vụ được giao | Đã có trong repo | Trạng thái | Commit |
|---|---|---|---|---|---|
| 1 | **Vỹ** | Git, cấu trúc package, tích hợp `BaseWindow`, hỗ trợ CSV | Khung project, package `vn.edu.ptit.oop.nhom03`, `.gitignore`, đổi tên package | 🟡 Thiếu **`common/BaseWindow.java`**, chưa ghép `Main` | 8 |
| 2 | **Hổ** | UML, `MenuItem`, `Drink`, `Food`, `Combo` (Composite) | `MenuItem`, `Drink`, `Food`, `Combo` (được đưa lên qua commit của Vỹ/Trâm) | 🟡 Code có, **chưa có UML**, chưa có commit tên mình | 0 |
| 3 | **Trâm** | `OrderItem`, `Order`, `SalesRepository` | `OrderItem`, `Order`, `OrderStatus`, `SalesRepository` | ✅ Tốt nhất nhóm: validate đầy đủ, `unmodifiableList`, chốt giá lúc gọi món | 4 |
| 4 | **Diễm Quỳnh** | `DiscountStrategy` + ≥ 3 cài đặt | `DiscountStrategy`, `NoDiscount`, `PercentDiscount`, `HappyHourDiscount`, `MemberDiscount` | ✅ Đủ 4 strategy, commit rõ ràng (có commit bị lặp do push 2 lần) | 12 |
| 5 | **Toàn** | `MainWindow`, `MenuPanel` (JTabbedPane + GridLayout + icon) | — | ❌ **Chưa có gì** | 0 |
| 6 | **Đạt** | JTable giỏ hàng, `CheckoutDialog`, in hóa đơn, định dạng VND | `CartPanel`, `CheckoutDialog` | 🟡 Có giao diện, còn lỗi logic (mục 4) | 1 |
| 7 | **Ánh** | `ReportWindow` + Bar/Line/Pie chart | `ReportWindow` (4 tab: bảng + 3 biểu đồ), thêm JAR JFreeChart | 🟡 Đủ 3 loại biểu đồ, chưa lọc theo ngày, chưa cập nhật động | 1 |
| 8 | **Bắc** | Báo cáo PDF, slide, kịch bản demo | — | ❌ Chưa có trong repo | 0 |
| 9 | **Phát** | `TablePanel`, `CsvMenuLoader`, `InvalidPaymentException`, `AboutDialog`, test case | `TablePanel`, `CafeTable`, `CsvMenuLoader`, `InvalidPaymentException`, `AboutDialog` | 🟡 Đủ lớp, nhưng exception **chưa được dùng**, CSV chưa trả về `MenuItem` | 2 |

> ⚠ Theo quy định: *"Mỗi thành viên đều phải viết code (ít nhất 2 lớp có nghĩa)"* và giảng viên xem lịch sử Git. Hiện **Hổ, Toàn, Bắc chưa có commit nào** đứng tên mình. Hổ nên tự commit (ví dụ UML hoặc chỉnh sửa model), Toàn cần làm `MainWindow` + `MenuPanel` ngay, Bắc cần ít nhất 2 lớp code (ví dụ `CartTableModel` hoặc `ChartHelper`).

---

## 3. Đối chiếu 8 bước của thẻ đề

| Bước | Yêu cầu của thầy | Hiện trạng | Đạt |
|---|---|---|---|
| 1 | `MenuItem` abstract (code, name, basePrice, category) + `getPrice()`; `Drink` (enum size **có hệ số giá**), `Food`, `Combo` chứa `List<MenuItem>` | Có đủ 4 lớp, Composite đúng. Nhưng `MenuItem` **không có** `basePrice`, `category`; enum `Size` không chứa hệ số mà dùng `switch` cộng tiền cố định | 🟡 |
| 2 | `OrderItem` (menuItem, quantity, note); `Order` (id, tableNo, `List<OrderItem>`, status enum, createdAt), `getSubtotal()`, `getTotal()` | Đủ và đúng | ✅ |
| 3 | `DiscountStrategy { double apply(Order o); }` + ≥ 3 cài đặt: No, Percent, HappyHour (14–16h, giảm 20% đồ uống), Member | Đủ 4 cài đặt | ✅ |
| 4 | `MainWindow extends BaseWindow`: trái `MenuPanel` (nút sinh tự động, GridLayout, icon, JTabbedPane theo danh mục); phải JTable giỏ hàng + tổng tiền | Chỉ có phần phải (`CartPanel`). Thiếu `BaseWindow`, `MainWindow`, `MenuPanel` | ❌ |
| 5 | `CheckoutDialog extends JDialog`: chọn KM, nhập tiền khách đưa → tiền thối; in hóa đơn vào JTextArea, định dạng VND bằng `NumberFormat` | Có đủ giao diện, còn lỗi (mục 4.1) | 🟡 |
| 6 | `SalesRepository` lưu đơn đã thanh toán; `ReportWindow extends JFrame` hiển thị đơn **theo ngày** trong JTable | Có cả hai, nhưng `CheckoutDialog` **không gọi** `save()`, `ReportWindow` không lọc theo ngày (`findByDate` chưa dùng) | 🟡 |
| 7 | BarChart top 5 món; LineChart doanh thu theo giờ; PieChart doanh thu theo danh mục | Đủ 3 biểu đồ, có tiêu đề và nhãn trục | ✅ (thiếu dữ liệu thật) |
| 8 (BONUS) | Sơ đồ bàn `TablePanel` vẽ bằng `paintComponent`, màu theo trạng thái; đọc menu từ CSV | Có `TablePanel` + `CsvMenuLoader` chạy thử bằng `main()` riêng, chưa tích hợp | 🟡 |

---

## 4. Lỗi và điểm cần sửa (xếp theo mức độ)

### 4.1. Lỗi nghiêm trọng – làm sai kịch bản demo

1. **Giảm 10% thực tế chỉ giảm 0,1%.** [CheckoutDialog.java:85](../src/vn/edu/ptit/oop/nhom03/ui/CheckoutDialog.java#L85) gọi `new PercentDiscount(0.1)`, trong khi `PercentDiscount.apply()` chia cho 100. → Sửa thành `new PercentDiscount(10)`.
2. **Đơn đã thanh toán không được lưu vào `SalesRepository`.** `processPayment()` chỉ gọi `markPaid()` rồi đóng dialog, nên `ReportWindow` sẽ luôn trống. Bước cuối của kịch bản demo ("mở báo cáo xem biểu đồ món bán chạy") sẽ không có gì để hiển thị. → Truyền `SalesRepository` vào `CheckoutDialog`/`CartPanel` và gọi `repo.save(order)` sau `markPaid()`.
3. **Thanh toán được khi chưa nhập tiền khách đưa.** [CheckoutDialog.java:94](../src/vn/edu/ptit/oop/nhom03/ui/CheckoutDialog.java#L94): ô trống thì nhãn hiện `0 ₫`, và `processPayment()` chỉ chặn hai chuỗi lỗi nên vẫn cho thanh toán. Ngoài ra nếu người dùng không chạm vào combobox thì `calculateChange()` chưa chạy lần nào.
4. **`InvalidPaymentException` chưa được dùng ở đâu cả.** Thầy yêu cầu *"ít nhất 1 exception tự định nghĩa"* và sẽ hỏi khi phản biện. → Nên tách hàm kiểu `PaymentService.pay(order, cash)` ném `InvalidPaymentException` khi tiền khách đưa < tổng tiền hoặc giỏ rỗng, rồi `CheckoutDialog` bắt và hiện `JOptionPane`. Hiện đang kiểm tra lỗi bằng cách **so sánh chữ trên JLabel**, cách này dễ bị hỏi vặn.
5. **Happy Hour phụ thuộc giờ tạo đơn.** `HappyHourDiscount` dùng `order.getCreatedAt()`. Nếu buổi bảo vệ không rơi vào 14h–16h thì demo sẽ giảm 0đ. → Chuẩn bị sẵn đơn mẫu tạo bằng `new Order(id, table, LocalDateTime.of(..., 15, 0))`, hoặc thêm hàm khởi tạo nhận `Clock`/giờ giả lập để demo.

### 4.2. Thiếu so với yêu cầu chung của thầy (mục 3, 4, 5 của tài liệu)

- **Thiếu `common/BaseWindow.java`**: đây là mã khởi đầu **bắt buộc**, phải chép nguyên văn và không được bỏ `initUI()`.
- **Thiếu `MainWindow extends BaseWindow`** dùng `BorderLayout` và `Main` dùng `SwingUtilities.invokeLater(...)`.
- **Thiếu menu File/View/Help (có mnemonic, phím tắt), toolbar có icon PNG 16×16 + tooltip, status bar, icon cửa sổ.** Chưa có thư mục `resources/icons/`.
- **Chưa có "dữ liệu thay đổi động và biểu đồ cập nhật theo".** `ReportWindow` chỉ đọc dữ liệu một lần trong constructor. → Thêm nút "Làm mới" hoặc dựng lại dataset mỗi khi mở cửa sổ hay khi lưu đơn mới.
- **Chưa có `ui/panels/`, `ui/dialogs/`, `util/ChartHelper.java`** như cấu trúc gợi ý. Hiện tất cả UI nằm chung trong `ui/`.
- **Project đang theo cấu trúc VS Code** (`.vscode/settings.json`, `bin/`), trong khi thầy dùng Eclipse. → Cần thêm `.project`/`.classpath` của Eclipse hoặc chuyển sang Maven, và thử mở bằng Eclipse trước buổi bảo vệ (Vỹ là người thao tác Eclipse khi phản biện).
- **Chưa có file JAR chạy được** (`app/OOP_NhomXX.jar`).

### 4.3. Điểm OOP dễ bị hỏi khi phản biện

| Vị trí | Vấn đề | Gợi ý |
|---|---|---|
| [Drink.java:28](../src/vn/edu/ptit/oop/nhom03/model/Drink.java#L28) | `switch (size)` + cộng 5.000/10.000 cố định; đề yêu cầu enum **có hệ số giá** | `enum Size { S(1.0), M(1.2), L(1.4); final double factor; }` → `getPrice() = basePrice * size.factor` (bỏ được `switch`) |
| [HappyHourDiscount.java:25](../src/vn/edu/ptit/oop/nhom03/service/HappyHourDiscount.java#L25) | Dùng `instanceof Drink`; thầy ghi rõ *"hạn chế dùng instanceof"* | So theo `getCategory()` hoặc thêm phương thức đa hình `isDrink()`/`isDiscountableInHappyHour()` vào `MenuItem` |
| `MenuItem` | Không kiểm tra `id`/`name` rỗng; thiếu `basePrice`, `category` theo đề | Thêm validate trong constructor; nếu giữ `getCategory()` trừu tượng thì giải thích rõ lý do |
| `Food` | Có trường `price` không `final`, không có setter, nên gán một lần là đủ | Đổi thành `final` |
| `Combo` | Giá = tổng giá món con, không có ưu đãi combo | Cân nhắc thêm `discountRate` cho combo; khi Drink trong combo đổi size thì giá combo cũng đổi theo |
| `CafeTable` | `status` là `int` 0/1/2 (magic number) | Đổi thành `enum TableStatus { EMPTY, OCCUPIED, RESERVED }` có màu riêng, `TablePanel` gọi `status.getColor()` (đa hình qua enum, bỏ `switch`) |
| [CartPanel.java:86](../src/vn/edu/ptit/oop/nhom03/ui/CartPanel.java#L86) | So enum bằng `name().equals("PAID")` | `currentOrder.getStatus() == OrderStatus.PAID` |
| `CartPanel` | Dùng `DefaultTableModel`; thẻ đề gợi ý `CartTableModel extends AbstractTableModel` (để có điểm kế thừa Swing) | Viết `CartTableModel` (có thể giao cho Bắc hoặc Toàn để đủ 2 lớp) |
| `CsvMenuLoader` | Trả về `List<String[]>` thay vì `List<MenuItem>`; `FileReader` dùng bảng mã mặc định nên **tiếng Việt có thể lỗi**; `catch` chỉ in ra console; có `main()` tạo file test | Đọc bằng `Files.newBufferedReader(path, UTF_8)`, parse thành `Drink`/`Food`, bỏ qua dòng lỗi, ném exception có thông báo rõ ràng |
| [AboutDialog.java:24](../src/vn/edu/ptit/oop/nhom03/ui/AboutDialog.java#L24) | Ghi "Nhóm 10"; package là `nhom03`, remote GitHub là `OOP_Nhom05_...` | Thống nhất số nhóm ở package, tên project, About, README, tên file zip |
| [ReportWindow.java:31](../src/vn/edu/ptit/oop/nhom03/ui/ReportWindow.java#L31) | `ChartFactory.setChartTheme` gọi trong constructor; tài liệu yêu cầu gọi **một lần trong `main()`** | Chuyển sang `Main` |
| `ReportWindow` | Pie chart dùng `oi.getSubtotal()` (giá trước khuyến mãi) nên tổng các phần khác tổng doanh thu | Ghi rõ "trước giảm giá" trong tiêu đề, hoặc phân bổ phần giảm giá theo tỷ lệ |
| Nhiều file | Thông báo lỗi lẫn có dấu và không dấu (`"Mon trong combo khong duoc null"`) | Thống nhất tiếng Việt có dấu |
| `TablePanel`, `AboutDialog`, `CsvMenuLoader` | Mỗi lớp có `main()` riêng để chạy thử | Xóa trước khi nộp, hoặc chuyển sang thư mục test |

### 4.4. Git và tài liệu

- Có commit lặp (các commit DiscountStrategy bị push hai lần), commit kiểu "Add files via upload" / "Frontend 2 Duy Đạt" không đúng quy ước. Từ giờ nên dùng `feat:`, `fix:`, `docs:`.
- `README.md` vẫn là mẫu VS Code. Cần ghi: đề tài, thành viên + MSSV, cách chạy, link video demo.
- `docs/` trống: cần sơ đồ UML (ảnh + file `.drawio`/`.puml`) và ảnh chụp màn hình.
- Chưa có commit "end of lab session" mà thầy yêu cầu ở giai đoạn 2.

---

## 5. Việc cần làm tiếp (ưu tiên từ trên xuống)

| Ưu tiên | Việc | Người phụ trách | Phụ thuộc |
|---|---|---|---|
| 🔴 1 | Thêm `common/BaseWindow.java` (chép nguyên văn của thầy), sửa `Main` dùng `invokeLater`, gọi `setChartTheme` | Vỹ | — |
| 🔴 2 | `MainWindow extends BaseWindow` (khởi tạo field **trong `initUI()`**, không gán tại chỗ khai báo) + `MenuPanel` (JTabbedPane: Nước uống / Đồ ăn / Combo, GridLayout, nút có icon) + menu, toolbar, status bar | Toàn (Vỹ hỗ trợ ghép) | 1 |
| 🔴 3 | Tạo dữ liệu menu mẫu (≥ 3 combo, đồ uống có hộp chọn size khi bấm) | Hổ / Phát (CSV) | — |
| 🔴 4 | Sửa `PercentDiscount(10)`, chặn ô tiền trống, gọi `salesRepo.save()`, dùng `InvalidPaymentException` | Đạt + Phát | 2 |
| 🟠 5 | `ReportWindow`: lọc theo ngày (`findByDate` + JComboBox/JSpinner ngày), nút làm mới, mở từ menu View/toolbar | Ánh | 2, 4 |
| 🟠 6 | Sinh sẵn 15–20 đơn mẫu ở nhiều khung giờ để biểu đồ có dữ liệu đẹp khi demo | Trâm / Ánh | — |
| 🟠 7 | Sửa enum `Size` có hệ số, bỏ `instanceof` trong HappyHour, `TableStatus` enum | Hổ, Quỳnh, Phát | — |
| 🟡 8 | `CsvMenuLoader` trả về `List<MenuItem>` + file `resources/menu.csv` (UTF-8), nạp khi khởi động | Phát + Vỹ | 2 |
| 🟡 9 | Gắn `TablePanel` vào MainWindow (chọn bàn → tạo `Order` với `tableNo`) | Phát | 2 |
| 🟡 10 | Icon PNG 16×16 / 32×32 vào `resources/icons/`, `app.png` cho thanh tiêu đề | Toàn | — |
| 🟡 11 | UML, README, ≥ 10 test case, báo cáo PDF, slide, video dự phòng, file JAR | Hổ, Bắc, Phát | 1–9 |
| 🟡 12 | Mở project bằng Eclipse, chạy thử kịch bản demo đầy đủ | Vỹ + Bắc | tất cả |

### Kịch bản demo bắt buộc – hiện trạng

| Bước demo | Chạy được chưa? | Thiếu gì |
|---|---|---|
| Gọi 1 đồ uống size L + 1 Combo | ❌ | `MenuPanel`, `MainWindow`, dữ liệu menu |
| Áp dụng Happy Hour | 🟡 | Logic có; cần giờ giả lập nếu bảo vệ ngoài 14h–16h |
| Thanh toán, tính tiền thối | 🟡 | Sửa các lỗi ở mục 4.1 |
| Mở báo cáo, xem biểu đồ món bán chạy | ❌ | Đơn chưa được lưu vào repository, chưa có nút mở `ReportWindow` |

---

## 6. Điểm mạnh hiện tại (nên nhấn mạnh khi thuyết trình)

- **Composite**: `Combo extends MenuItem` chứa `List<MenuItem>`, `getPrice()` cộng đệ quy giá các món con, có chặn combo tự chứa chính nó.
- **Strategy**: `Order` giữ tham chiếu `DiscountStrategy`, `getTotal()` gọi `apply()` theo đa hình. Đổi khuyến mãi lúc chạy mà không phải sửa `Order`.
- **Đóng gói tốt** ở `Order`/`OrderItem`: `getItems()` trả `unmodifiableList`; `setQuantity` để package-private nên chỉ `Order` sửa được; `checkEditable()` khóa đơn đã thanh toán; **chốt đơn giá lúc gọi món** để đổi size sau không làm sai đơn cũ (đây là quyết định thiết kế hay, nên đưa vào báo cáo).
- **Enum có hành vi**: `OrderStatus` có nhãn tiếng Việt, `MemberLevel` có tỷ lệ giảm.
- **JFreeChart** đủ 3 loại biểu đồ, có tiêu đề, nhãn trục, font hỗ trợ tiếng Việt; `ReportWindow` dùng `DISPOSE_ON_CLOSE` đúng lưu ý của thầy.
