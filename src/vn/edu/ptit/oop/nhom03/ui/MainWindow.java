package vn.edu.ptit.oop.nhom05.ui;

import vn.edu.ptit.oop.nhom05.common.BaseWindow;
import vn.edu.ptit.oop.nhom05.exception.InvalidOrderException;
import vn.edu.ptit.oop.nhom05.model.MenuItem;
import vn.edu.ptit.oop.nhom05.model.Order;
import vn.edu.ptit.oop.nhom05.model.OrderItem;
import vn.edu.ptit.oop.nhom05.model.OrderStatus;
import vn.edu.ptit.oop.nhom05.repository.InMemoryMenuRepository;
import vn.edu.ptit.oop.nhom05.repository.InMemorySalesRepository;
import vn.edu.ptit.oop.nhom05.repository.MenuRepository;
import vn.edu.ptit.oop.nhom05.repository.SalesRepository;
import vn.edu.ptit.oop.nhom05.service.*;
import vn.edu.ptit.oop.nhom05.util.MoneyFormat;
import vn.edu.ptit.oop.nhom05.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Cửa sổ chính của ứng dụng Quầy Bán Hàng Quán Cà Phê (MainWindow).
 * Phân hệ Frontend do Toàn phụ trách - ghép nối toàn bộ các màn hình của nhóm.
 * OOP:
 * - Kế thừa: Kế thừa BaseWindow của thầy.
 * - Đóng gói: Mọi field đều private, chỉ khởi tạo trong initUI() để tránh lỗi vòng đời Java Swing.
 * - Trừu tượng: Khai báo qua interface (MenuRepository, SalesRepository, DiscountStrategy).
 * - Kết hợp: MainWindow chứa MenuPanel, CartPanel, TablePanel, toolbar và các menu.
 * - Đa hình: Lắng nghe sự kiện thông qua các listener lồng.
 */
public class MainWindow extends BaseWindow {

    // LƯU Ý KỸ THUẬT PHẢN BIỆN THẦY CHẤM:
    // Tuyệt đối KHÔNG khởi tạo field tại chỗ (vd: private MenuRepository repo = new InMemoryMenuRepository();)
    // vì constructor BaseWindow gọi initUI() TRƯỚC KHI constructor của MainWindow hoàn tất.
    // Nếu gán tại chỗ, giá trị sẽ bị ghi đè thành null hoặc bị reset sau khi initUI() chạy.
    private MenuRepository menuRepository;
    private SalesRepository salesRepository;
    private List<DiscountStrategy> discountOptions;
    private Order currentOrder;

    private MenuPanel menuPanel;
    private CartPanel cartPanel;
    private TablePanel tablePanel;

    private JSpinner tableSpinner;
    private JCheckBoxMenuItem chkSimulateHappyHour;
    private JLabel lblClock;
    private JLabel lblHappyHourBadge;
    private Timer clockTimer;

    private boolean simulateHappyHour = false;
    private Supplier<LocalTime> clockSupplier;

    public MainWindow() {
        super("Quầy bán hàng Quán cà phê – Coffee Shop POS (PTIT Nhóm 05)", 1240, 740);
        // Sau khi constructor BaseWindow chạy xong và initUI() đã hoàn tất
        postInit();
    }

    @Override
    protected void initUI() {
        // 1. KHỞI TẠO DỮ LIỆU VÀ CÁC THÀNH PHẦN NGHIỆP VỤ (OOP: Interface-based programming)
        menuRepository = new InMemoryMenuRepository();
        salesRepository = new InMemorySalesRepository();

        // Cung cấp đồng hồ giả lập để kiểm thử Happy Hour (14h - 16h)
        clockSupplier = () -> simulateHappyHour ? LocalTime.of(15, 0) : LocalTime.now();

        discountOptions = new ArrayList<>();
        discountOptions.add(new NoDiscount());
        discountOptions.add(new PercentDiscount(0.10));
        discountOptions.add(new MemberDiscount());
        discountOptions.add(new HappyHourDiscount(clockSupplier));

        // Khởi tạo đơn hàng hiện tại cho Bàn 1
        currentOrder = new Order(1);
        currentOrder.setDiscountStrategy(discountOptions.get(0));

        // 2. KHỞI TẠO CÁC PANEL CON VÀ GHÉP NỐI LISTENER
        menuPanel = new MenuPanel(menuRepository);
        cartPanel = new CartPanel();
        tablePanel = new TablePanel();

        cartPanel.setDiscountOptions(discountOptions);
        cartPanel.setOrder(currentOrder);

        // Lắng nghe sự kiện chọn món từ MenuPanel
        menuPanel.setMenuItemSelectionListener(this::handleItemChosen);

        // Lắng nghe sự kiện giỏ hàng thay đổi từ CartPanel
        cartPanel.setCartListener(new CartPanel.CartListener() {
            @Override
            public void onCartChanged(String message) {
                setStatus(message);
            }

            @Override
            public void onOrderPaid(Order paidOrder) {
                handleOrderPaid(paidOrder);
            }
        });

        // Lắng nghe sự kiện chọn bàn từ TablePanel
        tablePanel.addTableSelectionListener(tableNo -> {
            if (tableSpinner != null) {
                tableSpinner.setValue(tableNo);
            }
            if (currentOrder != null) {
                currentOrder.setTableNo(tableNo);
            }
            setStatus("Đã chọn Bàn " + tableNo + " từ sơ đồ bàn.");
        });

        // 3. BỐ TRÍ TRUNG TÂM (CENTER) DÙNG JSPLITPANE
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPanel, cartPanel);
        splitPane.setResizeWeight(0.58); // Tỉ lệ 58% Menu bên trái, 42% Giỏ hàng bên phải
        splitPane.setDividerSize(6);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        // 4. BỔ SUNG CÁC MỤC VÀO MENU BAR CỦA BASEWINDOW
        customizeMenuBar();

        // 5. BỔ SUNG CÁC NÚT VÀO TOOLBAR CỦA BASEWINDOW
        customizeToolBar();

        // 6. BỔ SUNG ĐỒNG HỒ VÀ BADGE VÀO STATUS BAR CỦA BASEWINDOW
        customizeStatusBar();

        // 7. XỬ LÝ SỰ KIỆN ĐÓNG CỬA SỔ AN TOÀN
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    private void postInit() {
        // Khởi động đồng hồ thời gian thực
        clockTimer.start();
        updateClockAndBadge();
        setStatus("Sẵn sàng phục vụ khách hàng. Vui lòng chọn món trên thực đơn.");
    }

    /**
     * Bổ sung mục chọn vào MenuBar có sẵn từ BaseWindow.
     * Sử dụng phím tắt (Accelerator) và phím truy cập (Mnemonic) theo chuẩn.
     */
    private void customizeMenuBar() {
        // --- FILE MENU ---
        JMenuItem itemNewOrder = new JMenuItem("Đơn mới", KeyEvent.VK_N);
        itemNewOrder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        itemNewOrder.addActionListener(e -> newOrder());

        JMenuItem itemCheckout = new JMenuItem("Thanh toán", KeyEvent.VK_P);
        itemCheckout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        itemCheckout.addActionListener(e -> cartPanel.checkout());

        // Chèn vào đầu fileMenu để giữ Exit ở cuối cùng
        fileMenu.insert(itemNewOrder, 0);
        fileMenu.insert(itemCheckout, 1);
        fileMenu.insertSeparator(2);

        // --- VIEW MENU ---
        JMenuItem itemRefreshMenu = new JMenuItem("Làm mới menu (F5)", KeyEvent.VK_F5);
        itemRefreshMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        itemRefreshMenu.addActionListener(e -> {
            menuPanel.reload();
            setStatus("Đã làm mới danh mục thực đơn.");
        });

        JCheckBoxMenuItem chkToggleToolbar = new JCheckBoxMenuItem("Hiển thị thanh công cụ", true);
        chkToggleToolbar.addActionListener(e -> toolBar.setVisible(chkToggleToolbar.isSelected()));

        chkSimulateHappyHour = new JCheckBoxMenuItem("Giả lập giờ Happy Hour (14h-16h)", false);
        chkSimulateHappyHour.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        chkSimulateHappyHour.addActionListener(e -> {
            simulateHappyHour = chkSimulateHappyHour.isSelected();
            updateClockAndBadge();
            cartPanel.refreshCart();
            setStatus(simulateHappyHour ? "Đang bật giả lập giờ vàng (15:00)!" : "Đã chuyển về giờ thực tế.");
        });

        JMenuItem itemTableMap = new JMenuItem("Sơ đồ bàn...", KeyEvent.VK_T);
        itemTableMap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        itemTableMap.addActionListener(e -> openTableDialog());

        JMenuItem itemReport = new JMenuItem("Báo cáo doanh thu...", KeyEvent.VK_R);
        itemReport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        itemReport.addActionListener(e -> {
            // Phân hệ này do bạn Ánh (Thành viên 7) đảm nhiệm
            // TODO(REPORT-TEAMMATE): mở ReportWindow (JFrame, DISPOSE_ON_CLOSE)
            JOptionPane.showMessageDialog(this,
                    "Chức năng 'Báo cáo doanh thu & Biểu đồ' do thành viên khác phụ trách (Thành viên 7 - Ánh).\n" +
                            "Hiện đã lưu " + salesRepository.findAll().size() + " hóa đơn vào SalesRepository.",
                    "Báo cáo doanh thu", JOptionPane.INFORMATION_MESSAGE);
        });

        viewMenu.add(itemRefreshMenu);
        viewMenu.add(chkToggleToolbar);
        viewMenu.add(chkSimulateHappyHour);
        viewMenu.addSeparator();
        viewMenu.add(itemTableMap);
        viewMenu.add(itemReport);
    }

    /**
     * Bổ sung các nút bấm vào Toolbar có sẵn từ BaseWindow.
     */
    private void customizeToolBar() {
        addToolBarButton("/resources/icons/new.png", "Đơn mới (Ctrl+N)", e -> newOrder());
        addToolBarButton("/resources/icons/remove.png", "Xóa món", e -> cartPanel.removeSelected());
        addToolBarButton("/resources/icons/clear.png", "Xóa hết giỏ", e -> cartPanel.clearAll());
        addToolBarButton("/resources/icons/checkout.png", "Thanh toán (Ctrl+P)", e -> cartPanel.checkout());
        toolBar.addSeparator();

        addToolBarButton("/resources/icons/table.png", "Sơ đồ bàn (Ctrl+T)", e -> openTableDialog());
        addToolBarButton("/resources/icons/about.png", "Thông tin đề tài", e -> showAbout());

        toolBar.addSeparator();

        // Bộ chọn số bàn trực tiếp trên toolbar
        toolBar.add(new JLabel(" Bàn: "));
        tableSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        tableSpinner.setMaximumSize(new Dimension(65, 30));
        tableSpinner.addChangeListener(e -> {
            int newTable = (Integer) tableSpinner.getValue();
            if (currentOrder != null) {
                currentOrder.setTableNo(newTable);
                setStatus("Đã chuyển sang Bàn " + newTable);
            }
        });
        toolBar.add(tableSpinner);
    }

    /**
     * Bổ sung đồng hồ thời gian thực và huy hiệu Happy Hour vào góc phải Status Bar.
     */
    private void customizeStatusBar() {
        JPanel eastStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        eastStatusPanel.setOpaque(false);

        lblHappyHourBadge = new JLabel("● Giờ tiêu chuẩn");
        lblHappyHourBadge.setFont(Theme.FONT_BOLD);
        lblHappyHourBadge.setForeground(Theme.TEXT_MUTED);

        lblClock = new JLabel("00:00:00");
        lblClock.setFont(Theme.FONT_BOLD);
        lblClock.setForeground(Theme.PRIMARY);

        eastStatusPanel.add(lblHappyHourBadge);
        eastStatusPanel.add(new JSeparator(SwingConstants.VERTICAL));
        eastStatusPanel.add(lblClock);

        statusBar.add(eastStatusPanel, BorderLayout.EAST);

        // Sử dụng javax.swing.Timer (KHÔNG DÙNG Thread.sleep trong UI thread)
        clockTimer = new Timer(1000, e -> updateClockAndBadge());
    }

    private void updateClockAndBadge() {
        LocalTime now = clockSupplier.get();
        lblClock.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        int hour = now.getHour();
        boolean isHappyHour = (hour >= 14 && hour < 16);

        if (isHappyHour) {
            lblHappyHourBadge.setText("★ ĐANG GIỜ VÀNG HAPPY HOUR (-20% đồ uống)");
            lblHappyHourBadge.setForeground(new Color(220, 80, 0));
        } else {
            lblHappyHourBadge.setText("● Giờ bình thường");
            lblHappyHourBadge.setForeground(Theme.TEXT_MUTED);
        }
    }

    /**
     * Xử lý khi nhân viên click chọn món từ MenuPanel.
     * OOP: Đa hình kiểm tra hasSize() không dùng instanceof.
     */
    private void handleItemChosen(MenuItem item) {
        if (item == null) return;

        try {
            OrderItem chosenItem;
            // OOP: Đa hình qua phương thức hasSize()
            if (item.hasSize()) {
                // Mở ItemOptionDialog (form nhập liệu thứ nhất)
                chosenItem = ItemOptionDialog.ask(this, item);
                if (chosenItem == null) {
                    return; // Người dùng hủy chọn
                }
            } else {
                // Với món không có size (Food, Combo), tạo trực tiếp OrderItem 1 phần
                chosenItem = new OrderItem(item, 1, "");
            }

            currentOrder.addItem(chosenItem);

            // Đánh dấu bàn hiện tại chuyển sang trạng thái "Có khách" (Đỏ: 1)
            tablePanel.setTableStatus(currentOrder.getTableNo(), 1);

            cartPanel.refreshCart();
            setStatus(String.format("Đã thêm %d x %s vào giỏ hàng Bàn %d.",
                    chosenItem.getQuantity(), chosenItem.getMenuItem().getName(), currentOrder.getTableNo()));
        } catch (InvalidOrderException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể thêm món: " + ex.getMessage(),
                    "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi hệ thống khi thêm món: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý khi thanh toán đơn hàng thành công từ CheckoutDialog.
     */
    private void handleOrderPaid(Order paidOrder) {
        if (paidOrder == null) return;

        try {
            // Lưu hóa đơn vào kho SalesRepository
            salesRepository.save(paidOrder);

            // Cập nhật trạng thái bàn về "Trống" (Xanh lá: 0)
            tablePanel.setTableStatus(paidOrder.getTableNo(), 0);

            setStatus(String.format("Thanh toán thành công đơn %s (Bàn %d) - Tổng tiền: %s",
                    paidOrder.getId(), paidOrder.getTableNo(), MoneyFormat.format(paidOrder.getTotal())));

            // Tạo đơn hàng mới cho bàn tiếp theo
            int nextTable = paidOrder.getTableNo();
            currentOrder = new Order(nextTable);
            currentOrder.setDiscountStrategy(discountOptions.get(0));
            cartPanel.setOrder(currentOrder);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi lưu trữ đơn hàng: " + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tạo mới đơn hàng (làm trống giỏ hàng).
     */
    public void newOrder() {
        if (currentOrder != null && !currentOrder.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Giỏ hàng hiện tại đang có món. Bạn có chắc muốn tạo đơn mới và hủy món hiện tại?",
                    "Xác nhận tạo đơn mới", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int tableNo = (Integer) tableSpinner.getValue();
        currentOrder = new Order(tableNo);
        currentOrder.setDiscountStrategy(discountOptions.get(0));
        cartPanel.setOrder(currentOrder);
        setStatus("Đã tạo mới đơn hàng cho Bàn " + tableNo);
    }

    /**
     * Mở sơ đồ bàn dạng modal dialog để chọn bàn nhanh.
     */
    private void openTableDialog() {
        JDialog dialog = new JDialog(this, "Sơ đồ bàn quán cà phê", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(tablePanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSelect = new JButton("Chọn bàn này");
        btnSelect.setBackground(Theme.PRIMARY);
        btnSelect.setForeground(Color.WHITE);
        btnSelect.addActionListener(e -> {
            int selectedNo = tablePanel.getSelectedTableNo();
            tableSpinner.setValue(selectedNo);
            if (currentOrder != null) {
                currentOrder.setTableNo(selectedNo);
            }
            dialog.dispose();
        });
        btnPanel.add(btnSelect);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    protected void showAbout() {
        // OOP: Đa hình override showAbout của BaseWindow để mở AboutDialog đầy đủ của nhóm
        AboutDialog dialog = new AboutDialog(this);
        dialog.setVisible(true);
    }

    private void handleExit() {
        if (currentOrder != null && !currentOrder.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Đang có đơn hàng chưa thanh toán. Bạn có chắc muốn thoát chương trình?",
                    "Xác nhận thoát", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Dừng timer để giải phóng tài nguyên
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
        dispose();
        System.exit(0);
    }
}
