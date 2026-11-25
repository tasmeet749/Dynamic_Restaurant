import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
/*
 * ============================================================
 * DYNAMIC RESTAURANT ORDER & KITCHEN WORKFLOW SYSTEM
 * Sprint 3 Prototype
 * ============================================================
 *
 * Architectural Style:
 * Layered Architecture
 *
 * Presentation
 *      ↓
 * Business Logic
 *      ↓
 * Data Access
 *      ↓
 * In-Memory Storage
 *
 * Notification Module
 *      ↑
 * Business Logic / Kitchen Workflow
 *
 * Features:
 * - Login
 * - Menu
 * - Create Order
 * - Multiple Items
 * - Billing
 * - Priority
 * - Kitchen Stations
 * - Preparation Dependencies
 * - Delays
 * - Cancellation
 * - Order Completion
 * - Notifications
 * - Validation
 *
 * Login:
 * admin / 1234
 * staff / staff123
 * kitchen / kitchen123
 *
 * Logo:
 * images.jpg
 */


/* ============================================================
 * MAIN
 * ============================================================
 */

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }
            RestaurantSystem system =
                    new RestaurantSystem();

            LoginFrame login =
                    new LoginFrame(system);

            login.setVisible(true);
        });
    }
}


/* ============================================================
 * APPLICATION SYSTEM
 * ============================================================
 */

class RestaurantSystem {

    UserRepository userRepository;
    MenuRepository menuRepository;
    OrderRepository orderRepository;
    StationRepository stationRepository;

    MenuService menuService;
    OrderService orderService;
    BillCalculator billCalculator;
    PriorityManager priorityManager;
    DependencyManager dependencyManager;
    StaffNotification notification;
    KitchenCoordinator kitchenCoordinator;
    CancellationManager cancellationManager;
    DelayManager delayManager;

    public RestaurantSystem() {

        userRepository = new UserRepository();
        menuRepository = new MenuRepository();
        orderRepository = new OrderRepository();
        stationRepository = new StationRepository();

        menuService =
                new MenuService(menuRepository);

        orderService =
                new OrderService(
                        orderRepository,
                        menuRepository
                );

        billCalculator =
                new BillCalculator();

        priorityManager =
                new PriorityManager();

        dependencyManager =
                new DependencyManager();

        notification =
                new StaffNotification();

        kitchenCoordinator =
                new KitchenCoordinator(
                        orderRepository,
                        stationRepository,
                        dependencyManager,
                        notification
                );
                cancellationManager =
        new CancellationManager(
                notification,
                orderRepository,
                stationRepository
        );
        delayManager =
               new DelayManager(notification, orderRepository);
    }
}


/* ============================================================
 * COLORS / UI HELPERS
 * ============================================================
 */

class UI {

// ============================================================
// COLORS
// ============================================================

    static final Color NAVY =
            new Color(24, 34, 52);

    static final Color NAVY_LIGHT =
            new Color(39, 53, 77);

    static final Color ORANGE =
            new Color(224, 122, 55);

    static final Color ORANGE_DARK =
            new Color(190, 91, 30);

    static final Color CREAM =
            new Color(248, 246, 242);

    static final Color WHITE =
            Color.WHITE;

    static final Color TEXT =
            new Color(20, 23, 28);

    static final Color MUTED =
            new Color(45, 50, 58);

    static final Color BORDER =
            new Color(210, 213, 218);

    static final Color GREEN =
            new Color(30, 125, 70);

    static final Color RED =
            new Color(180, 45, 45);

    static final Color BLUE =
            new Color(40, 90, 160);

    static final Color YELLOW =
            new Color(190, 130, 25);
// =========================
    // FONTS
    // =========================

    static final Font TITLE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    22
            );

    static final Font SUBTITLE =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    16
            );

    static final Font NORMAL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    14
            );

    static final Font SMALL =
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    12
            );

    static final Font BOLD =
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    14
            );


    // =========================
    // BUTTON
    // =========================

    // =========================
// BUTTON  (auto dark/light text)
// =========================

    static JButton button(
            String text,
            Color background) {

        JButton b =
                new JButton(text);

        b.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        // ---- DARK TEXT for light backgrounds ----
        Color darkText =
                new Color(20, 20, 24);

        Color textColor =
                isDarkColor(background)
                        ? Color.WHITE
                        : darkText;

        b.setForeground(textColor);

        b.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        b.setBackground(background);

        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.setBorder(
                new EmptyBorder(
                        10,
                        20,
                        10,
                        20
                )
        );

        b.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        b.addMouseListener(
                new MouseAdapter() {

                    public void mouseEntered(
                            MouseEvent e) {

                        Color hoverBg =
                                background.darker();

                        b.setBackground(hoverBg);

                        b.setForeground(
                                isDarkColor(hoverBg)
                                        ? Color.WHITE
                                        : darkText
                        );
                    }

                    public void mouseExited(
                            MouseEvent e) {

                        b.setBackground(
                                background
                        );

                        b.setForeground(textColor);
                    }
                }
        );

        return b;
    }


    // Helper: background DARK hai ya LIGHT?
    static boolean isDarkColor(Color c) {

        int brightness =
                (int) Math.sqrt(
                        0.241 * c.getRed() * c.getRed()
                                + 0.691 * c.getGreen() * c.getGreen()
                                + 0.068 * c.getBlue() * c.getBlue()
                );

        return brightness < 130;
    } // =========================
    // HEADING
    // =========================

    static JLabel heading(
            String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(TITLE);
        label.setForeground(TEXT);

        return label;
    }


    // =========================
    // CARD
    // =========================

    static JPanel card() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(WHITE);

        panel.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                BORDER,
                                1
                        ),
                        new EmptyBorder(
                                16,
                                18,
                                16,
                                18
                        )
                )
        );

        return panel;
    }


    // =========================
    // TABLE STYLE
    // =========================

    static void styleTable(
            JTable table) {

        table.setFont(NORMAL);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);

        table.getTableHeader()
                .setFont(BOLD);

        table.getTableHeader()
                .setBackground(NAVY);

        table.getTableHeader()
                .setForeground(Color.WHITE);

        table.setSelectionBackground(
                new Color(
                        252,
                        231,
                        215
                )
        );

        table.setSelectionForeground(TEXT);

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setBorder(
                new EmptyBorder(
                        0,
                        8,
                        0,
                        8
                )
        );

        table.setDefaultRenderer(
                Object.class,
                renderer
        );
    }
}


/* ============================================================
 * USER MODEL
 * ============================================================
 */

class User {

    private String username;
    private String role;

    public User(
            String username,
            String role) {

        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}


/* ============================================================
 * MENU ITEM
 * ============================================================
 */

class MenuItem {

    private int id;
    private String name;
    private String category;
    private double price;
    private String station;
    private boolean available;

    public MenuItem(
            int id,
            String name,
            String category,
            double price,
            String station) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.station = station;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getStation() {
        return station;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(
            boolean available) {

        this.available = available;
    }

    @Override
    public String toString() {

        return name +
                "   |   Rs. " +
                String.format(
                        "%.2f",
                        price
                );
    }
}


/* ============================================================
 * ORDER ITEM
 * ============================================================
 */

class OrderItem {

    private MenuItem menuItem;
    private int quantity;

    public OrderItem(
            MenuItem menuItem,
            int quantity) {

        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {

        return menuItem.getPrice()
                * quantity;
    }
}


/* ============================================================
 * PREPARATION TASK
 * ============================================================
 */

class PreparationTask {

    private int taskId;
    private int orderId;
    private String itemName;
    private String stationName;
    private String status;
    private int dependencyTaskId;
    private int delayMinutes;
    private String delayReason;

    public PreparationTask(
            int taskId,
            int orderId,
            String itemName,
            String stationName) {

        this.taskId = taskId;
        this.orderId = orderId;
        this.itemName = itemName;
        this.stationName = stationName;

        this.status = "WAITING";
        this.dependencyTaskId = -1;
        this.delayMinutes = 0;
        this.delayReason = "";
    }

    public int getTaskId() {
        return taskId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStatus() {
        return status;
    }

    public int getDependencyTaskId() {
        return dependencyTaskId;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public String getDelayReason() {
        return delayReason;
    }

    public void setStatus(
            String status) {

        this.status = status;
    }

    public void setDependencyTaskId(
            int id) {

        this.dependencyTaskId = id;
    }

    public void setDelayMinutes(
            int minutes) {

        this.delayMinutes = minutes;
    }

    public void setDelayReason(
            String reason) {

        this.delayReason = reason;
    }
}


/* ============================================================
 * ORDER
 * ============================================================
 */

class Order {

    private int orderId;
    private int tableNumber;
    private String priority;
    private String status;
    private Date orderDate;

    private List<OrderItem> items;
    private List<PreparationTask> tasks;

    private String cancellationReason;

    public Order(
            int orderId,
            int tableNumber,
            String priority) {

        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.priority = priority;
        this.status = "NEW";

        this.orderDate = new Date();

        this.items =
                new ArrayList<>();

        this.tasks =
                new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public List<PreparationTask> getTasks() {
        return tasks;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setStatus(
            String status) {

        this.status = status;
    }

    public void setPriority(
            String priority) {

        this.priority = priority;
    }

    public void setCancellationReason(
            String reason) {

        this.cancellationReason = reason;
    }

    public void addItem(
            OrderItem item) {

        items.add(item);
    }

    public void addTask(
            PreparationTask task) {

        tasks.add(task);
    }

    public double getSubtotal() {

        double total = 0;

        for (OrderItem item : items) {
            total += item.getTotal();
        }

        return total;
    }

    public boolean allTasksCompleted() {

        if (tasks.isEmpty()) {
            return false;
        }

        for (PreparationTask task :
                tasks) {

            if (!task.getStatus()
                    .equals("COMPLETED")) {

                return false;
            }
        }

        return true;
    }
}


/* ============================================================
 * KITCHEN STATION
 * ============================================================
 */

class KitchenStation {

    private int id;
    private String name;
    private boolean available;

    public KitchenStation(
            int id,
            String name) {

        this.id = id;
        this.name = name;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(
            boolean available) {

        this.available = available;
    }
}


/* ============================================================
 * USER REPOSITORY
 * ============================================================
 */

class UserRepository {

    private Map<String, String> passwords;
    private Map<String, String> roles;

    public UserRepository() {

        passwords =
                new HashMap<>();

        roles =
                new HashMap<>();

        passwords.put(
                "admin",
                "1234"
        );

        roles.put(
                "admin",
                "ADMIN"
        );

        passwords.put(
                "staff",
                "staff123"
        );

        roles.put(
                "staff",
                "STAFF"
        );

        passwords.put(
                "kitchen",
                "kitchen123"
        );

        roles.put(
                "kitchen",
                "KITCHEN"
        );
    }

    public User authenticate(
            String username,
            String password) {

        if (!passwords.containsKey(
                username)) {

            return null;
        }

        if (!passwords.get(username)
                .equals(password)) {

            return null;
        }

        return new User(
                username,
                roles.get(username)
        );
    }
}


/* ============================================================
 * MENU REPOSITORY
 * ============================================================
 */

class MenuRepository {

    private List<MenuItem> menuItems;

    public MenuRepository() {

        menuItems =
                new ArrayList<>();

        menuItems.add(
                new MenuItem(
                        1,
                        "Burger",
                        "Fast Food",
                        500,
                        "GRILL"
                )
        );

        menuItems.add(
                new MenuItem(
                        2,
                        "Pizza",
                        "Fast Food",
                        900,
                        "OVEN"
                )
        );

        menuItems.add(
                new MenuItem(
                        3,
                        "Fries",
                        "Sides",
                        300,
                        "FRY"
                )
        );

        menuItems.add(
                new MenuItem(
                        4,
                        "Biryani",
                        "Main Course",
                        350,
                        "HOT FOOD"
                )
        );

        menuItems.add(
                new MenuItem(
                        5,
                        "Pasta",
                        "Main Course",
                        650,
                        "PASTA"
                )
        );

        menuItems.add(
                new MenuItem(
                        6,
                        "Coke",
                        "Drinks",
                        120,
                        "DRINKS"
                )
        );

        menuItems.add(
                new MenuItem(
                        7,
                        "Juice",
                        "Drinks",
                        200,
                        "DRINKS"
                )
        );
    }

    public List<MenuItem> getAll() {
        return menuItems;
    }

    public MenuItem findByName(
            String name) {

        for (MenuItem item :
                menuItems) {

            if (item.getName()
                    .equalsIgnoreCase(name)) {

                return item;
            }
        }

        return null;
    }
}


/* ============================================================
 * ORDER REPOSITORY
 * ============================================================
 */


class OrderRepository {

    private final List<Order> orders = new ArrayList<>();

    public OrderRepository() {
        loadFromDatabase();
    }

    public synchronized void save(Order order) {
        String orderSql = "INSERT INTO restaurant_orders " +
                "(table_number, priority, status, cancellation_reason) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items " +
                "(order_id, item_id, item_name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                int generatedId;
                try (PreparedStatement ps = con.prepareStatement(
                        orderSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, order.getTableNumber());
                    ps.setString(2, order.getPriority());
                    ps.setString(3, order.getStatus());
                    ps.setString(4, order.getCancellationReason());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("MySQL did not generate an order ID.");
                        }
                        generatedId = keys.getInt(1);
                    }
                }

                // Ensure each hardcoded prototype menu item also exists in MySQL
                // before inserting order_items (some schemas enforce a foreign key).
                String menuSql = "INSERT INTO menu_items " +
                        "(item_id, name, category, price, station_name, available) " +
                        "VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE item_id = item_id";
                try (PreparedStatement menuPs = con.prepareStatement(menuSql)) {
                    for (OrderItem item : order.getItems()) {
                        MenuItem menuItem = item.getMenuItem();
                        menuPs.setInt(1, menuItem.getId());
                        menuPs.setString(2, menuItem.getName());
                        menuPs.setString(3, menuItem.getCategory());
                        menuPs.setBigDecimal(4, java.math.BigDecimal.valueOf(menuItem.getPrice()));
                        menuPs.setString(5, menuItem.getStation());
                        menuPs.addBatch();
                    }
                    menuPs.executeBatch();
                }

                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    for (OrderItem item : order.getItems()) {
                        ps.setInt(1, generatedId);
                        ps.setInt(2, item.getMenuItem().getId());
                        ps.setString(3, item.getMenuItem().getName());
                        ps.setInt(4, item.getQuantity());
                        ps.setBigDecimal(5, java.math.BigDecimal.valueOf(item.getMenuItem().getPrice()));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                order.setOrderId(generatedId);
                orders.add(order);
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                throw ex;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not save order to MySQL: " + ex.getMessage(), ex);
        }
    }

    public synchronized void saveTasks(Order order) {
        String sql = "INSERT INTO preparation_tasks " +
                "(order_id, task_number, item_name, station_name, status, " +
                "dependency_task_number, delay_minutes, delay_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (PreparationTask task : order.getTasks()) {
                ps.setInt(1, order.getOrderId());
                ps.setInt(2, task.getTaskId());
                ps.setString(3, task.getItemName());
                ps.setString(4, task.getStationName());
                ps.setString(5, task.getStatus());
                if (task.getDependencyTaskId() == -1) {
                    ps.setNull(6, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(6, task.getDependencyTaskId());
                }
                ps.setInt(7, task.getDelayMinutes());
                ps.setString(8, task.getDelayReason());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not save kitchen tasks to MySQL: " + ex.getMessage(), ex);
        }
    }

    public synchronized void update(Order order) {
        String orderSql = "UPDATE restaurant_orders SET status = ?, cancellation_reason = ? WHERE order_id = ?";
        String taskSql = "UPDATE preparation_tasks SET status = ?, dependency_task_number = ?, " +
                "delay_minutes = ?, delay_reason = ? WHERE order_id = ? AND task_number = ?";
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(orderSql)) {
                    ps.setString(1, order.getStatus());
                    ps.setString(2, order.getCancellationReason());
                    ps.setInt(3, order.getOrderId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(taskSql)) {
                    for (PreparationTask task : order.getTasks()) {
                        ps.setString(1, task.getStatus());
                        if (task.getDependencyTaskId() == -1) ps.setNull(2, java.sql.Types.INTEGER);
                        else ps.setInt(2, task.getDependencyTaskId());
                        ps.setInt(3, task.getDelayMinutes());
                        ps.setString(4, task.getDelayReason());
                        ps.setInt(5, order.getOrderId());
                        ps.setInt(6, task.getTaskId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                throw ex;
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not update order in MySQL: " + ex.getMessage(), ex);
        }
    }

    public synchronized List<Order> getAll() {
        return new ArrayList<>(orders);
    }

    public synchronized Order findById(int id) {
        for (Order order : orders) {
            if (order.getOrderId() == id) return order;
        }
        return null;
    }

    private void loadFromDatabase() {
        String orderSql = "SELECT order_id, table_number, priority, status, cancellation_reason " +
                "FROM restaurant_orders ORDER BY order_id DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(orderSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(rs.getInt("order_id"),
                        rs.getInt("table_number"), rs.getString("priority"));
                order.setStatus(rs.getString("status"));
                order.setCancellationReason(rs.getString("cancellation_reason"));
                loadItems(con, order);
                loadTasks(con, order);
                orders.add(order);
            }
        } catch (SQLException ex) {
            // Allow the UI to open if the database is temporarily unavailable.
            System.err.println("Could not load saved orders: " + ex.getMessage());
        }
    }

    private void loadItems(Connection con, Order order) throws SQLException {
        String sql = "SELECT oi.item_id, oi.item_name, oi.quantity, oi.unit_price, " +
                "COALESCE(mi.category, 'Saved Order') AS item_category, " +
                "COALESCE(mi.station_name, 'UNKNOWN') AS station_name " +
                "FROM order_items oi LEFT JOIN menu_items mi ON mi.item_id = oi.item_id " +
                "WHERE oi.order_id = ? ORDER BY oi.order_item_id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, order.getOrderId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(rs.getInt("item_id"),
                            rs.getString("item_name"), rs.getString("item_category"),
                            rs.getBigDecimal("unit_price").doubleValue(), rs.getString("station_name"));
                    order.addItem(new OrderItem(item, rs.getInt("quantity")));
                }
            }
        }
    }

    private void loadTasks(Connection con, Order order) throws SQLException {
        String sql = "SELECT task_number, item_name, station_name, status, " +
                "dependency_task_number, delay_minutes, delay_reason " +
                "FROM preparation_tasks WHERE order_id = ? ORDER BY task_number";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, order.getOrderId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PreparationTask task = new PreparationTask(rs.getInt("task_number"),
                            order.getOrderId(), rs.getString("item_name"), rs.getString("station_name"));
                    task.setStatus(rs.getString("status"));
                    int dependency = rs.getInt("dependency_task_number");
                    task.setDependencyTaskId(rs.wasNull() ? -1 : dependency);
                    task.setDelayMinutes(rs.getInt("delay_minutes"));
                    task.setDelayReason(rs.getString("delay_reason"));
                    order.addTask(task);
                }
            }
        }
    }
}

/* ============================================================
 * STATION REPOSITORY
 * ============================================================
 */

class StationRepository {

    private final List<KitchenStation> stations = new ArrayList<>();

    public StationRepository() {
        loadStationsFromDatabase();
        if (stations.isEmpty()) {
            addDefaultStations();
        }
    }

    private void addDefaultStations() {
        stations.clear();
        stations.add(new KitchenStation(1, "GRILL"));
        stations.add(new KitchenStation(2, "OVEN"));
        stations.add(new KitchenStation(3, "FRY"));
        stations.add(new KitchenStation(4, "HOT FOOD"));
        stations.add(new KitchenStation(5, "PASTA"));
        stations.add(new KitchenStation(6, "DRINKS"));
    }

    private void loadStationsFromDatabase() {
        String sql = "SELECT station_id, name, available FROM kitchen_stations ORDER BY station_id";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KitchenStation station = new KitchenStation(
                        rs.getInt("station_id"), rs.getString("name"));
                station.setAvailable(rs.getBoolean("available"));
                stations.add(station);
            }
        } catch (SQLException ex) {
            // Keep the existing prototype usable if MySQL is temporarily unavailable.
            System.err.println("Could not load kitchen stations from MySQL: " + ex.getMessage());
        }
    }

    public List<KitchenStation> getAll() {
        return Collections.unmodifiableList(stations);
    }

    public KitchenStation findByName(String name) {
        if (name == null) return null;
        for (KitchenStation station : stations) {
            if (station.getName().equalsIgnoreCase(name.trim())) {
                return station;
            }
        }
        return null;
    }

    /** Changes the in-memory state and persists it to the existing kitchen_stations table. */
    public synchronized void setAvailability(KitchenStation station, boolean available) {
        if (station == null) return;
        boolean oldValue = station.isAvailable();
        station.setAvailable(available);
        String sql = "UPDATE kitchen_stations SET available = ? WHERE station_id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, station.getId());
            int changed = ps.executeUpdate();
            if (changed == 0) {
                // Existing station name may be present with a different ID; try matching by name.
                try (PreparedStatement byName = con.prepareStatement(
                        "UPDATE kitchen_stations SET available = ? WHERE name = ?")) {
                    byName.setBoolean(1, available);
                    byName.setString(2, station.getName());
                    byName.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            station.setAvailable(oldValue);
            throw new IllegalStateException(
                    "Could not update kitchen station availability in MySQL: " + ex.getMessage(), ex);
        }
    }
}

/* ============================================================
 * MENU SERVICE
 * ============================================================
 */

class MenuService {

    private MenuRepository repository;

    public MenuService(
            MenuRepository repository) {

        this.repository = repository;
    }

    public List<MenuItem> getMenu() {
        return repository.getAll();
    }

    public MenuItem findItem(
            String name) {

        return repository.findByName(name);
    }
}


/* ============================================================
 * PRIORITY MANAGER
 * ============================================================
 */

class PriorityManager {

    public boolean isValid(
            String priority) {

        return priority != null &&
                (
                        priority.equals("NORMAL") ||
                                priority.equals("HIGH") ||
                                priority.equals("URGENT")
                );
    }

    public int getPriorityValue(
            String priority) {

        switch (priority) {

            case "URGENT":
                return 3;

            case "HIGH":
                return 2;

            default:
                return 1;
        }
    }
}


/* ============================================================
 * BILL CALCULATOR
 * ============================================================
 */

class BillCalculator {

    private static final double TAX_RATE =
            0.05;

    public double calculateSubtotal(
            Order order) {

        return order.getSubtotal();
    }

    public double calculateTax(
            double subtotal) {

        return subtotal * TAX_RATE;
    }

    public double calculateTotal(
            double subtotal) {

        return subtotal +
                calculateTax(subtotal);
    }
}


/* ============================================================
 * ORDER SERVICE
 * ============================================================
 */

class OrderService {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;

    public OrderService(
            OrderRepository orderRepository,
            MenuRepository menuRepository) {

        this.orderRepository =
                orderRepository;

        this.menuRepository =
                menuRepository;
    }

    public Order createOrder(
            int tableNumber,
            String priority) {

        if (tableNumber <= 0) {

            throw new IllegalArgumentException(
                    "Table number must be greater than 0."
            );
        }

        if (priority == null ||
                priority.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Priority is required."
            );
        }

        return new Order(
                0,
                tableNumber,
                priority
        );
    }

    public void addItem(
            Order order,
            String itemName,
            int quantity) {

        if (itemName == null ||
                itemName.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Food item is required."
            );
        }

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        MenuItem item =
                menuRepository.findByName(
                        itemName
                );

        if (item == null) {

            throw new IllegalArgumentException(
                    "Food item does not exist."
            );
        }

        if (!item.isAvailable()) {

            throw new IllegalArgumentException(
                    "Food item is currently unavailable."
            );
        }

        order.addItem(
                new OrderItem(
                        item,
                        quantity
                )
        );
    }

    public void save(
            Order order) {

        if (order.getItems()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Order must contain at least one item."
            );
        }

        orderRepository.save(order);
    }
}


/* ============================================================
 * DEPENDENCY MANAGER
 * ============================================================
 */

class DependencyManager {

    public boolean canStart(
            PreparationTask task,
            Order order) {

        if (task.getDependencyTaskId() == -1) {
            return true;
        }

        for (PreparationTask other :
                order.getTasks()) {

            if (other.getTaskId() ==
                    task.getDependencyTaskId()) {

                return other.getStatus()
                        .equals("COMPLETED");
            }
        }

        return false;
    }
}


/* ============================================================
 * KITCHEN COORDINATOR
 * ============================================================
 */

class KitchenCoordinator {

    private OrderRepository orderRepository;
    private StationRepository stationRepository;
    private DependencyManager dependencyManager;
    private StaffNotification notification;

    public KitchenCoordinator(
            OrderRepository orderRepository,
            StationRepository stationRepository,
            DependencyManager dependencyManager,
            StaffNotification notification) {

        this.orderRepository =
                orderRepository;

        this.stationRepository =
                stationRepository;

        this.dependencyManager =
                dependencyManager;

        this.notification =
                notification;
    }

    public void createTasks(
            Order order) {

        int taskId = 1;

        for (OrderItem item :
                order.getItems()) {

            PreparationTask task =
                    new PreparationTask(
                            taskId,
                            order.getOrderId(),
                            item.getMenuItem()
                                    .getName(),
                            item.getMenuItem()
                                    .getStation()
                    );

            order.addTask(task);

            taskId++;
        }

        /*
         * Prototype dependency model:
         * Every task depends on the previous task.
         */

        for (int i = 1;
             i < order.getTasks().size();
             i++) {

            order.getTasks()
                    .get(i)
                    .setDependencyTaskId(
                            order.getTasks()
                                    .get(i - 1)
                                    .getTaskId()
                    );
        }

        orderRepository.saveTasks(order);
    }

    public void acceptOrder(
            Order order) {

        if (order == null) {
            throw new IllegalArgumentException("Order is required.");
        }
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Cancelled order cannot be accepted.");
        }
        if ("SERVED".equalsIgnoreCase(order.getStatus()) ||
                "READY".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Ready or served order cannot be accepted again.");
        }

        order.setStatus("ACCEPTED");

        notification.notifyStaff(
                "Order #" +
                        order.getOrderId() +
                        " accepted by kitchen."
        );

        orderRepository.update(order);
    }

    public String startTask(
            Order order,
            int taskId) {

        PreparationTask task =
                findTask(
                        order,
                        taskId
                );

        if (task == null) {
            return "Task not found.";
        }

        if (order == null) return "Order not found.";
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            return "Cancelled order cannot be processed.";
        }
        if ("SERVED".equalsIgnoreCase(order.getStatus()) ||
                "READY".equalsIgnoreCase(order.getStatus())) {
            return "Ready or served order cannot be processed.";
        }
        if ("CANCELLED".equalsIgnoreCase(task.getStatus())) {
            return "Cancelled task cannot be started.";
        }
        if ("COMPLETED".equalsIgnoreCase(task.getStatus())) {
            return "Completed task cannot be started again.";
        }
        if ("PREPARING".equalsIgnoreCase(task.getStatus())) {
            return "Task is already PREPARING.";
        }
        if ("DELAYED".equalsIgnoreCase(task.getStatus())) {
            return "Resume the delayed task before starting it.";
        }

        if (!dependencyManager
                .canStart(task, order)) {

            return
                    "Task cannot start because its dependency is not completed.";
        }

        KitchenStation station =
                stationRepository.findByName(
                        task.getStationName()
                );

        if (station == null) {
            return "Kitchen station not found.";
        }

        if (!station.isAvailable()) {

            return
                    "Station " +
                            station.getName() +
                            " is currently BUSY.";
        }

        try {
            stationRepository.setAvailability(station, false);
        } catch (IllegalStateException ex) {
            return ex.getMessage();
        }

        String previousTaskStatus = task.getStatus();
        String previousOrderStatus = order.getStatus();
        task.setStatus("PREPARING");
        order.setStatus("PREPARING");
        try {
            orderRepository.update(order);
        } catch (IllegalStateException ex) {
            task.setStatus(previousTaskStatus);
            order.setStatus(previousOrderStatus);
            try { stationRepository.setAvailability(station, true); }
            catch (IllegalStateException ignored) { }
            return ex.getMessage();
        }

        notification.notifyStaff(
                "Task #" +
                        task.getTaskId() +
                        " for Order #" +
                        order.getOrderId() +
                        " is PREPARING at " +
                        station.getName() +
                        "."
        );

        return "Task started successfully.";
    }

    public String completeTask(
            Order order,
            int taskId) {

        PreparationTask task =
                findTask(
                        order,
                        taskId
                );

        if (task == null) {
            return "Task not found.";
        }

        if ("CANCELLED".equalsIgnoreCase(order.getStatus()) ||
                "CANCELLED".equalsIgnoreCase(task.getStatus())) {
            return "Cancelled orders or tasks cannot be completed.";
        }

        if (!"PREPARING".equalsIgnoreCase(task.getStatus())) {

            return
                    "Task must be PREPARING before it can be completed.";
        }

        KitchenStation station =
                stationRepository.findByName(
                        task.getStationName()
                );

        if (station != null) {
            try {
                stationRepository.setAvailability(station, true);
            } catch (IllegalStateException ex) {
                return ex.getMessage();
            }
        }

        task.setStatus("COMPLETED");
        // Persist every task transition, not only the final READY order transition.
        try {
            orderRepository.update(order);
        } catch (IllegalStateException ex) {
            task.setStatus("PREPARING");
            if (station != null) {
                try { stationRepository.setAvailability(station, false); }
                catch (IllegalStateException ignored) { }
            }
            return ex.getMessage();
        }

        notification.notifyStaff(
                "Task #" +
                        task.getTaskId() +
                        " completed for Order #" +
                        order.getOrderId() +
                        "."
        );

        checkCompletion(order);

        return "Task completed successfully.";
    }

    private void checkCompletion(
            Order order) {

        if (order.allTasksCompleted()) {

            order.setStatus("READY");

            orderRepository.update(order);

            notification.notifyStaff(
                    "Order #" +
                            order.getOrderId() +
                            " is READY for serving."
            );
        }
    }

    private PreparationTask findTask(
            Order order,
            int taskId) {

        for (PreparationTask task :
                order.getTasks()) {

            if (task.getTaskId() == taskId) {
                return task;
            }
        }

        return null;
    }
}


/* ============================================================
 * CANCELLATION MANAGER
 * ============================================================
 */


class CancellationManager {

    private final StaffNotification notification;
    private final OrderRepository repository;
    private final StationRepository stationRepository;

    public CancellationManager(
            StaffNotification notification,
            OrderRepository repository,
            StationRepository stationRepository) {
        this.notification = notification;
        this.repository = repository;
        this.stationRepository = stationRepository;
    }

    public String cancelOrder(
            Order order,
            String reason) {

        if (order == null) return "Order not found.";
        if ("SERVED".equalsIgnoreCase(order.getStatus())) {
            return "Served order cannot be cancelled.";
        }
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            return "Order is already cancelled.";
        }

        if (reason == null || reason.trim().isEmpty()) {
            return "Cancellation reason is required.";
        }

        String previousOrderStatus = order.getStatus();
        String previousReason = order.getCancellationReason();
        Map<PreparationTask, String> previousTaskStatuses = new IdentityHashMap<>();
        List<KitchenStation> stationsToRelease = new ArrayList<>();
        order.setStatus("CANCELLED");
        order.setCancellationReason(reason.trim());

        for (PreparationTask task : order.getTasks()) {
            previousTaskStatuses.put(task, task.getStatus());
            if (!"COMPLETED".equalsIgnoreCase(task.getStatus())) {
                if ("PREPARING".equalsIgnoreCase(task.getStatus())) {
                    KitchenStation station = stationRepository.findByName(task.getStationName());
                    if (station != null) stationsToRelease.add(station);
                }
                task.setStatus("CANCELLED");
            }
        }

        try {
            repository.update(order);
        } catch (Exception e) {
            order.setStatus(previousOrderStatus);
            order.setCancellationReason(previousReason);
            for (Map.Entry<PreparationTask, String> entry : previousTaskStatuses.entrySet()) {
                entry.getKey().setStatus(entry.getValue());
            }
            return "Cancellation could not be saved to database: " + e.getMessage();
        }

        String stationWarning = "";
        for (KitchenStation station : stationsToRelease) {
            try {
                stationRepository.setAvailability(station, true);
            } catch (IllegalStateException ex) {
                stationWarning = " Order was cancelled, but station availability needs checking: "
                        + station.getName() + ".";
                System.err.println(ex.getMessage());
            }
        }

        notification.notifyStaff(
                "Order #" + order.getOrderId()
                + " CANCELLED.\nReason: " + reason.trim()
        );

        return "Order cancelled and saved successfully." + stationWarning;
    }
}

/* ============================================================
 * DELAY MANAGER
 * ============================================================
 */
class DelayManager {

    private StaffNotification notification;
    private OrderRepository repository;

    public DelayManager(
            StaffNotification notification,
            OrderRepository repository) {

        this.notification = notification;
        this.repository = repository;
    }

    public String addDelay(
            Order order,
            int taskId,
            int minutes,
            String reason) {

        if (minutes <= 0) {

            return
                    "Delay must be greater than zero.";
        }

        if (reason == null ||
                reason.trim().isEmpty()) {

            return
                    "Delay reason is required.";
        }

        PreparationTask task = null;

        for (PreparationTask t :
                order.getTasks()) {

            if (t.getTaskId() == taskId) {

                task = t;
                break;
            }
        }

        if (task == null) {
            return "Task not found.";
        }
        if (order == null || "CANCELLED".equalsIgnoreCase(order.getStatus()) ||
                "SERVED".equalsIgnoreCase(order.getStatus()) ||
                "READY".equalsIgnoreCase(order.getStatus())) {
            return "This order cannot be delayed in its current state.";
        }
        if (!"PREPARING".equalsIgnoreCase(task.getStatus())) {
            return "Only a PREPARING task can be marked as delayed.";
        }

       String oldOrderStatus = order.getStatus();
       String oldTaskStatus = task.getStatus();
       int oldDelayMinutes = task.getDelayMinutes();
       String oldDelayReason = task.getDelayReason();
       task.setStatus("DELAYED");
       task.setDelayMinutes(minutes);
       task.setDelayReason(reason.trim());
       order.setStatus("DELAYED");
       try {
           repository.update(order);
       } catch (IllegalStateException ex) {
           task.setStatus(oldTaskStatus);
           task.setDelayMinutes(oldDelayMinutes);
           task.setDelayReason(oldDelayReason);
           order.setStatus(oldOrderStatus);
           return ex.getMessage();
       }

       notification.notifyStaff(
                "Order #" +
                        order.getOrderId() +
                        " DELAYED.\n" +
                        "Task: " +
                        task.getItemName() +
                        "\nDelay: " +
                        minutes +
                        " minutes\nReason: " +
                        reason
        );

        return "Delay recorded successfully.";
    }

    public String resumeTask(
            Order order,
            int taskId) {

        for (PreparationTask task :
                order.getTasks()) {

            if (task.getTaskId() == taskId) {

                if (!"DELAYED".equalsIgnoreCase(task.getStatus())) {
                    return "Task is not delayed.";
                }
                if (order == null || "CANCELLED".equalsIgnoreCase(order.getStatus()) ||
                        "SERVED".equalsIgnoreCase(order.getStatus()) ||
                        "READY".equalsIgnoreCase(order.getStatus())) {
                    return "This order cannot be resumed in its current state.";
                }

               task.setStatus("PREPARING");
               order.setStatus("PREPARING");
               try {
                   repository.update(order);
               } catch (IllegalStateException ex) {
                   task.setStatus("DELAYED");
                   order.setStatus("DELAYED");
                   return ex.getMessage();
               }

               notification.notifyStaff(
                        "Task #" +
                                task.getTaskId() +
                                " resumed."
                );

                return
                        "Task resumed successfully.";
            }
        }

        return "Task not found.";
    }
}


/* ============================================================
 * NOTIFICATION
 * ============================================================
 */

class StaffNotification {

    private List<String> messages;

    public StaffNotification() {

        messages =
                new ArrayList<>();
    }

    public void notifyStaff(
            String message) {

        String formatted =
                new Date() +
                        "  -  " +
                        message;

        messages.add(formatted);

        System.out.println(
                "[NOTIFICATION] " +
                        message
        );
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }
}


/* ============================================================
 * LOGIN FRAME
 * ============================================================
 */

class LoginFrame extends JFrame {

    private RestaurantSystem system;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(
            RestaurantSystem system) {

        this.system = system;

        setTitle(
                "Dynamic Restaurant | Staff Login"
        );

        setSize(
                900,
                560
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setResizable(false);

        buildUI();
    }

    private void buildUI() {

        JPanel root =
                new JPanel(
                        new BorderLayout()
                );

        root.setBackground(
                UI.CREAM
        );

        /* LEFT BRAND AREA */

        JPanel brand =
                new JPanel(
                        new BorderLayout()
                );

        brand.setPreferredSize(
                new Dimension(
                        420,
                        560
                )
        );

        brand.setBackground(
                UI.NAVY
        );

        JPanel logoArea =
                new JPanel(
                        new GridBagLayout()
                );

        logoArea.setOpaque(false);

        JLabel logo =
                loadLogo(
                        240,
                        190
                );

        logoArea.add(logo);

        brand.add(
                logoArea,
                BorderLayout.CENTER
        );

        JPanel brandText =
                new JPanel(
                        new BorderLayout()
                );

        brandText.setOpaque(false);

        JLabel systemName =
                new JLabel(
                        "<html><center>" +
                                "<b>DYNAMIC RESTAURANT</b><br>" +
                                "<font size='4'>ORDER & KITCHEN WORKFLOW</font>" +
                                "</center></html>",
                        SwingConstants.CENTER
                );

        systemName.setForeground(
                Color.WHITE
        );

        systemName.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        19
                )
        );

        brandText.setBorder(
                new EmptyBorder(
                        0,
                        25,
                        35,
                        25
                )
        );

        brandText.add(
                systemName,
                BorderLayout.CENTER
        );

        brand.add(
                brandText,
                BorderLayout.SOUTH
        );

        root.add(
                brand,
                BorderLayout.WEST
        );


        /* RIGHT LOGIN */

        JPanel right =
                new JPanel(
                        new GridBagLayout()
                );

        right.setBackground(
                UI.CREAM
        );

        JPanel form =
                new JPanel();

        form.setLayout(
                new BoxLayout(
                        form,
                        BoxLayout.Y_AXIS
                )
        );

        form.setBackground(
                UI.CREAM
        );

        form.setPreferredSize(
                new Dimension(
                        350,
                        390
                )
        );

        JLabel welcome =
                new JLabel(
                        "Welcome Back"
                );

        welcome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        welcome.setForeground(
                UI.TEXT
        );

        JLabel subtitle =
                new JLabel(
                        "Sign in to manage restaurant operations"
                );

        subtitle.setFont(UI.NORMAL);

        subtitle.setForeground(
                UI.MUTED
        );

        form.add(welcome);

        form.add(
                Box.createVerticalStrut(6)
        );

        form.add(subtitle);

        form.add(
                Box.createVerticalStrut(32)
        );

        JLabel userLabel =
                new JLabel("USERNAME");

        userLabel.setFont(
                UI.BOLD
        );

        usernameField =
                new JTextField();

        usernameField.setFont(UI.NORMAL);

        usernameField.setPreferredSize(
                new Dimension(
                        340,
                        42
                )
        );

        usernameField.setMaximumSize(
                new Dimension(
                        340,
                        42
                )
        );

        form.add(userLabel);

        form.add(
                Box.createVerticalStrut(7)
        );

        form.add(usernameField);

        form.add(
                Box.createVerticalStrut(18)
        );

        JLabel passLabel =
                new JLabel("PASSWORD");

        passLabel.setFont(UI.BOLD);

        passwordField =
                new JPasswordField();

        passwordField.setFont(UI.NORMAL);

        passwordField.setPreferredSize(
                new Dimension(
                        340,
                        42
                )
        );

        passwordField.setMaximumSize(
                new Dimension(
                        340,
                        42
                )
        );

        form.add(passLabel);

        form.add(
                Box.createVerticalStrut(7)
        );

        form.add(passwordField);

        form.add(
                Box.createVerticalStrut(25)
        );

        JButton loginButton =
                UI.button(
                        "LOGIN TO SYSTEM",
                        UI.ORANGE
                );

        loginButton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        loginButton.setMaximumSize(
                new Dimension(
                        340,
                        45
                )
        );

        loginButton.addActionListener(
                e -> login()
        );

        form.add(loginButton);

        form.add(
                Box.createVerticalStrut(25)
        );

        JLabel demo =
                new JLabel(
                        "<html><b>Demo Accounts</b><br>" +
                                "Admin: admin / 1234<br>" +
                                "Staff: staff / staff123<br>" +
                                "Kitchen: kitchen / kitchen123</html>"
                );

        demo.setFont(UI.SMALL);
        demo.setForeground(UI.MUTED);

        form.add(demo);

        right.add(form);

        root.add(
                right,
                BorderLayout.CENTER
        );

        setContentPane(root);

        getRootPane().setDefaultButton(
                loginButton
        );
    }

    private JLabel loadLogo(
            int width,
            int height) {

        ImageIcon icon =
                new ImageIcon(
                        "images.jpg"
                );

        if (icon.getIconWidth() <= 0) {

            JLabel missing =
                    new JLabel(
                            "RESTAURANT",
                            SwingConstants.CENTER
                    );

            missing.setForeground(
                    Color.WHITE
            );

            missing.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            28
                    )
            );

            return missing;
        }

        Image image =
                icon.getImage();

        int originalWidth =
                icon.getIconWidth();

        int originalHeight =
                icon.getIconHeight();

        double scale =
                Math.min(
                        (double) width /
                                originalWidth,
                        (double) height /
                                originalHeight
                );

        int newWidth =
                (int)
                        (originalWidth * scale);

        int newHeight =
                (int)
                        (originalHeight * scale);

        Image scaled =
                image.getScaledInstance(
                        newWidth,
                        newHeight,
                        Image.SCALE_SMOOTH
                );

        return new JLabel(
                new ImageIcon(scaled)
        );
    }

    private void login() {

        String username =
                usernameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        if (username.isEmpty()) {

            error(
                    "Username cannot be empty."
            );

            return;
        }

        if (password.isEmpty()) {

            error(
                    "Password cannot be empty."
            );

            return;
        }

        User user =
                system.userRepository
                        .authenticate(
                                username,
                                password
                        );

        if (user == null) {

            error(
                    "Incorrect username or password."
            );

            return;
        }

        dispose();

        MainDashboard dashboard =
                new MainDashboard(
                        system,
                        user
                );

        dashboard.setVisible(true);
    }

    private void error(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Login Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


/* ============================================================
 * MAIN DASHBOARD
 * ============================================================
 */

class MainDashboard extends JFrame {

    private RestaurantSystem system;
    private User user;

    private JPanel contentPanel;

    private JLabel pageTitle;
    private JLabel pageSubtitle;

    private JButton dashboardButton;
    private JButton createOrderButton;
    private JButton kitchenButton;
    private JButton ordersButton;
    private JButton menuButton;
    private JButton notificationButton;

    public MainDashboard(
            RestaurantSystem system,
            User user) {

        this.system = system;
        this.user = user;

        setTitle(
                "Dynamic Restaurant Order & Kitchen Workflow Coordination System"
        );

        setSize(
                1280,
                780
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        buildUI();
    }

    private void buildUI() {

        JPanel root =
                new JPanel(
                        new BorderLayout()
                );

        root.setBackground(
                UI.CREAM
        );


        /* ====================================================
         * TOP NAVIGATION
         * ====================================================
         */

        JPanel topBar =
                new JPanel(
                        new BorderLayout()
                );

        topBar.setPreferredSize(
                new Dimension(
                        0,
                        88
                )
        );

        topBar.setBackground(
                UI.NAVY
        );

        topBar.setBorder(
                new EmptyBorder(
                        8,
                        20,
                        8,
                        20
                )
        );


        /* LOGO - NOT CHANGED */

        JLabel logo =
                loadLogo(
                        100,
                        65
                );

        JPanel logoPanel =
                new JPanel(
                        new GridBagLayout()
                );

        logoPanel.setOpaque(false);

        logoPanel.setPreferredSize(
                new Dimension(
                        120,
                        72
                )
        );

        logoPanel.add(logo);

        topBar.add(
                logoPanel,
                BorderLayout.WEST
        );


        /* TITLE */

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setOpaque(false);

        JLabel title =
                new JLabel(
                        "DYNAMIC RESTAURANT"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        title.setForeground(
                Color.WHITE
        );

        JLabel subtitle =
                new JLabel(
                        "Order & Kitchen Workflow Coordination"
                );

        subtitle.setFont(UI.SMALL);

        subtitle.setForeground(
                new Color(
                        205,
                        211,
                        220
                )
        );

        titlePanel.add(title);

        titlePanel.add(
                Box.createVerticalStrut(2)
        );

        titlePanel.add(subtitle);

        topBar.add(
                titlePanel,
                BorderLayout.CENTER
        );


        /* USER */

        JPanel userPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        userPanel.setOpaque(false);

        JLabel userName =
                new JLabel(
                        user.getUsername(),
                        SwingConstants.RIGHT
                );

        userName.setForeground(
                Color.WHITE
        );

        userName.setFont(
                UI.BOLD
        );

        JLabel role =
                new JLabel(
                        user.getRole(),
                        SwingConstants.RIGHT
                );

        role.setForeground(
                new Color(
                        205,
                        211,
                        220
                )
        );

        role.setFont(UI.SMALL);

        userPanel.add(userName);
        userPanel.add(role);

        topBar.add(
                userPanel,
                BorderLayout.EAST
        );

        root.add(
                topBar,
                BorderLayout.NORTH
        );


        /* ====================================================
         * SIDE NAVIGATION
         * ====================================================
         */

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setPreferredSize(
                new Dimension(
                        225,
                        0
                )
        );

        sidebar.setBackground(
                new Color(
                        30,
                        42,
                        61
                )
        );

        JPanel nav =
                new JPanel();

        nav.setLayout(
                new BoxLayout(
                        nav,
                        BoxLayout.Y_AXIS
                )
        );

        nav.setBackground(
                new Color(
                        30,
                        42,
                        61
                )
        );

        nav.setBorder(
                new EmptyBorder(
                        22,
                        12,
                        10,
                        12
                )
        );

        JLabel navTitle =
                new JLabel(
                        "RESTAURANT MENU"
                );

        navTitle.setForeground(
                Color.WHITE
        );

        navTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        nav.add(navTitle);

        nav.add(
                Box.createVerticalStrut(15)
        );

        dashboardButton =
                navButton("DASHBOARD");

        createOrderButton =
                navButton("CREATE ORDER");

        kitchenButton =
                navButton("KITCHEN WORKFLOW");

        ordersButton =
                navButton("ALL ORDERS");

        menuButton =
                navButton("MENU");

        notificationButton =
                navButton("NOTIFICATIONS");

       
String userRole = user.getRole().toUpperCase();

if (userRole.equals("ADMIN")) {
    nav.add(dashboardButton);
    nav.add(createOrderButton);
    nav.add(kitchenButton);
    nav.add(ordersButton);
    nav.add(menuButton);
    nav.add(notificationButton);

} else if (userRole.equals("STAFF")) {
    nav.add(createOrderButton);
    nav.add(ordersButton);
    nav.add(notificationButton);

} else if (userRole.equals("KITCHEN")) {
    nav.add(kitchenButton);
    nav.add(ordersButton);
    nav.add(notificationButton);
}

        dashboardButton.addActionListener(
                e -> showDashboard()
        );

        createOrderButton.addActionListener(
                e -> showCreateOrder()
        );

        kitchenButton.addActionListener(
                e -> showKitchen()
        );

        ordersButton.addActionListener(
                e -> showOrders()
        );

        menuButton.addActionListener(
                e -> showMenu()
        );

        notificationButton.addActionListener(
                e -> showNotifications()
        );

        sidebar.add(
                nav,
                BorderLayout.NORTH
        );


        JPanel bottomNav =
                new JPanel(
                        new BorderLayout()
                );

        bottomNav.setBackground(
                new Color(
                        30,
                        42,
                        61
                )
        );

        bottomNav.setBorder(
                new EmptyBorder(
                        10,
                        12,
                        18,
                        12
                )
        );

        JButton logout =
                navButton("LOGOUT");

        logout.addActionListener(
                e -> logout()
        );

        bottomNav.add(
                logout,
                BorderLayout.CENTER
        );

        sidebar.add(
                bottomNav,
                BorderLayout.SOUTH
        );

        root.add(
                sidebar,
                BorderLayout.WEST
        );


        /* ====================================================
         * CONTENT
         * ====================================================
         */

        JPanel mainArea =
                new JPanel(
                        new BorderLayout()
                );

        mainArea.setBackground(
                UI.CREAM
        );

        JPanel pageHeader =
                new JPanel(
                        new BorderLayout()
                );

        pageHeader.setBackground(
                UI.CREAM
        );

        pageHeader.setBorder(
                new EmptyBorder(
                        25,
                        30,
                        18,
                        30
                )
        );

        JPanel headingPanel =
                new JPanel();

        headingPanel.setLayout(
                new BoxLayout(
                        headingPanel,
                        BoxLayout.Y_AXIS
                )
        );

        headingPanel.setOpaque(false);

        pageTitle =
                new JLabel(
                        "Dashboard"
                );

        pageTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                )
        );

        pageTitle.setForeground(
                UI.TEXT
        );

        pageSubtitle =
                new JLabel(
                        "Overview of restaurant operations"
                );

        pageSubtitle.setFont(UI.NORMAL);

        pageSubtitle.setForeground(
                UI.MUTED
        );

        headingPanel.add(pageTitle);

        headingPanel.add(
                Box.createVerticalStrut(4)
        );

        headingPanel.add(pageSubtitle);

        pageHeader.add(
                headingPanel,
                BorderLayout.WEST
        );

        mainArea.add(
                pageHeader,
                BorderLayout.NORTH
        );

        contentPanel =
                new JPanel(
                        new BorderLayout()
                );

        contentPanel.setBackground(
                UI.CREAM
        );

        contentPanel.setBorder(
                new EmptyBorder(
                        0,
                        30,
                        25,
                        30
                )
        );

        mainArea.add(
                contentPanel,
                BorderLayout.CENTER
        );

        root.add(
                mainArea,
                BorderLayout.CENTER
        );

      
setContentPane(root);

String currentRole = user.getRole().toUpperCase();

if (currentRole.equals("ADMIN")) {
    showDashboard();
} else if (currentRole.equals("STAFF")) {
    showCreateOrder();
} else if (currentRole.equals("KITCHEN")) {
    showKitchen();
}

    }

    private JButton navButton(
            String text) {

        JButton b =
                new JButton(text);

        b.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        // CLEAR WHITE TEXT
        b.setForeground(Color.WHITE);

        b.setBackground(
                new Color(
                        30,
                        42,
                        61
                )
        );

        b.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        b.setMaximumSize(
                new Dimension(
                        200,
                        45
                )
        );

        b.setPreferredSize(
                new Dimension(
                        200,
                        45
                )
        );

        b.setBorder(
                new EmptyBorder(
                        0,
                        16,
                        0,
                        10
                )
        );

        b.setFocusPainted(false);

        b.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        b.addMouseListener(
                new MouseAdapter() {

                    public void mouseEntered(
                            MouseEvent e) {

                        b.setBackground(
                                UI.NAVY_LIGHT
                        );

                        b.setForeground(
                                Color.WHITE
                        );
                    }

                    public void mouseExited(
                            MouseEvent e) {

                        b.setBackground(
                                new Color(
                                        30,
                                        42,
                                        61
                                )
                        );

                        b.setForeground(
                                Color.WHITE
                        );
                    }
                }
        );

        b.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return b;
    }

    private JLabel loadLogo(
            int width,
            int height) {

        ImageIcon icon =
                new ImageIcon(
                        "images.jpg"
                );

        if (icon.getIconWidth() <= 0) {

            JLabel label =
                    new JLabel(
                            "LOGO"
                    );

            label.setForeground(
                    Color.WHITE
            );

            return label;
        }

        int originalWidth =
                icon.getIconWidth();

        int originalHeight =
                icon.getIconHeight();

        double scale =
                Math.min(
                        (double) width /
                                originalWidth,
                        (double) height /
                                originalHeight
                );

        int newWidth =
                Math.max(
                        1,
                        (int)
                                (originalWidth * scale)
                );

        int newHeight =
                Math.max(
                        1,
                        (int)
                                (originalHeight * scale)
                );

        Image scaled =
                icon.getImage()
                        .getScaledInstance(
                                newWidth,
                                newHeight,
                                Image.SCALE_SMOOTH
                        );

        return new JLabel(
                new ImageIcon(scaled)
        );
    }

    private void showDashboard() {

        pageTitle.setText(
                "Dashboard"
        );

        pageSubtitle.setText(
                "Overview of restaurant operations"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new DashboardPanel(system),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCreateOrder() {

        pageTitle.setText(
                "Create Order"
        );

        pageSubtitle.setText(
                "Create customer orders and generate bills"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new OrderPanel(system),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showKitchen() {

        pageTitle.setText(
                "Kitchen Workflow"
        );

        pageSubtitle.setText(
                "Coordinate preparation tasks and kitchen stations"
        );

        contentPanel.removeAll();

        contentPanel.add(
               new KitchenPanel(system, user.getRole()),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOrders() {

        pageTitle.setText(
                "All Orders"
        );

        pageSubtitle.setText(
                "Monitor current and completed restaurant orders"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new OrdersPanel(system),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMenu() {

        pageTitle.setText(
                "Restaurant Menu"
        );

        pageSubtitle.setText(
                "Available food items and assigned kitchen stations"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new MenuPanel(system),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNotifications() {

        pageTitle.setText(
                "Notifications"
        );

        pageSubtitle.setText(
                "Order and kitchen workflow updates"
        );

        contentPanel.removeAll();

        contentPanel.add(
                new NotificationPanel(system),
                BorderLayout.CENTER
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (result ==
                JOptionPane.YES_OPTION) {

            dispose();

            new LoginFrame(system)
                    .setVisible(true);
        }
    }
}


/* ============================================================
 * DASHBOARD PANEL
 * ============================================================
 */

class DashboardPanel extends JPanel {

    private RestaurantSystem system;

    public DashboardPanel(
            RestaurantSystem system) {

        this.system = system;

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                UI.CREAM
        );

        buildUI();
    }

    private void buildUI() {

        JPanel stats =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                15,
                                0
                        )
                );

        stats.setOpaque(false);

        int total =
                system.orderRepository
                        .getAll()
                        .size();

        int preparing = 0;
        int ready = 0;
        int delayed = 0;

        for (Order order :
                system.orderRepository
                        .getAll()) {

            if (order.getStatus()
                    .equals("PREPARING")) {

                preparing++;
            }

            if (order.getStatus()
                    .equals("READY")) {

                ready++;
            }

            if (order.getStatus()
                    .equals("DELAYED")) {

                delayed++;
            }
        }

        stats.add(
                statCard(
                        "TOTAL ORDERS",
                        String.valueOf(total),
                        UI.BLUE
                )
        );

        stats.add(
                statCard(
                        "PREPARING",
                        String.valueOf(preparing),
                        UI.ORANGE
                )
        );

        stats.add(
                statCard(
                        "READY",
                        String.valueOf(ready),
                        UI.GREEN
                )
        );

        stats.add(
                statCard(
                        "DELAYED",
                        String.valueOf(delayed),
                        UI.RED
                )
        );

        JPanel center =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                        )
                );

        center.setOpaque(false);

        JPanel recent =
                UI.card();

        JLabel recentTitle =
                new JLabel(
                        "Recent Orders"
                );

        recentTitle.setFont(
                UI.SUBTITLE
        );

        recentTitle.setForeground(
                UI.TEXT
        );

        recent.add(
                recentTitle,
                BorderLayout.NORTH
        );

        DefaultTableModel model =
                new DefaultTableModel(
                        new String[]{
                                "Order",
                                "Table",
                                "Priority",
                                "Status",
                                "Amount"
                        },
                        0
                ) {

                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        JTable table =
                new JTable(model);

        UI.styleTable(table);

        List<Order> orders =
                system.orderRepository
                        .getAll();

        int start =
                Math.max(
                        0,
                        orders.size() - 8
                );

        for (int i = start;
             i < orders.size();
             i++) {

            Order order =
                    orders.get(i);

            model.addRow(
                    new Object[]{
                            "#" +
                                    order.getOrderId(),
                            "Table " +
                                    order.getTableNumber(),
                            order.getPriority(),
                            order.getStatus(),
                            String.format(
                                    "Rs. %.2f",
                                    system.billCalculator
                                            .calculateTotal(
                                                    order.getSubtotal()
                                            )
                            )
                    }
            );
        }

        recent.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JPanel workflow =
                UI.card();

        JLabel workflowTitle =
                new JLabel(
                        "Kitchen Workflow"
                );

        workflowTitle.setFont(
                UI.SUBTITLE
        );

        workflow.add(
                workflowTitle,
                BorderLayout.NORTH
        );

        JLabel workflowText =
                new JLabel(
                        "<html>" +
                                "<b>NEW</b> &nbsp; → &nbsp; " +
                                "<b>ACCEPTED</b> &nbsp; → &nbsp; " +
                                "<b>PREPARING</b> &nbsp; → &nbsp; " +
                                "<b>READY</b> &nbsp; → &nbsp; " +
                                "<b>SERVED</b>" +
                                "<br><br>" +
                                "Orders can also be handled through " +
                                "priority management, dependencies, " +
                                "delays and cancellation." +
                                "</html>"
                );

        workflowText.setFont(UI.NORMAL);
        workflowText.setForeground(UI.MUTED);

        workflow.add(
                workflowText,
                BorderLayout.CENTER
        );

        center.add(
                recent,
                BorderLayout.CENTER
        );

        center.add(
                workflow,
                BorderLayout.SOUTH
        );

        add(
                stats,
                BorderLayout.NORTH
        );

        add(
                center,
                BorderLayout.CENTER
        );
    }

    private JPanel statCard(
            String title,
            String value,
            Color color) {

        JPanel card =
                UI.card();

        JPanel accent =
                new JPanel();

        accent.setBackground(color);

        accent.setPreferredSize(
                new Dimension(
                        6,
                        60
                )
        );

        JPanel text =
                new JPanel();

        text.setLayout(
                new BoxLayout(
                        text,
                        BoxLayout.Y_AXIS
                )
        );

        text.setOpaque(false);

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        titleLabel.setForeground(
                UI.MUTED
        );

        JLabel valueLabel =
                new JLabel(value);

        valueLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        valueLabel.setForeground(
                UI.TEXT
        );

        text.add(titleLabel);

        text.add(
                Box.createVerticalStrut(4)
        );

        text.add(valueLabel);

        card.add(
                accent,
                BorderLayout.WEST
        );

        card.add(
                text,
                BorderLayout.CENTER
        );

        return card;
    }
}


/* ============================================================
 * ORDER PANEL
 * ============================================================
 */

class OrderPanel extends JPanel {

    private RestaurantSystem system;

    private JComboBox<Integer> tableCombo;
    private JComboBox<String> priorityCombo;
    private JComboBox<MenuItem> itemCombo;
    private JSpinner quantitySpinner;

    private DefaultTableModel orderModel;
    private JTable orderTable;

    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;

    public OrderPanel(
            RestaurantSystem system) {

        this.system = system;

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                UI.CREAM
        );

        buildUI();
    }

    private void buildUI() {

        JPanel top =
                UI.card();

        JPanel fields = new JPanel(
                new GridLayout(
                        2,
                        4,
                        12,
                        12
                )
        );

        JLabel tableLabel =
                new JLabel(
                        "TABLE NUMBER"
                );

        tableLabel.setFont(UI.BOLD);

        fields.add(tableLabel);

        tableCombo =
                new JComboBox<>();

        for (int i = 1;
             i <= 20;
             i++) {

            tableCombo.addItem(i);
        }

        fields.add(tableCombo);

        JLabel priorityLabel =
                new JLabel(
                        "PRIORITY"
                );

        priorityLabel.setFont(UI.BOLD);

        fields.add(priorityLabel);

        priorityCombo =
                new JComboBox<>(
                        new String[]{
                                "NORMAL",
                                "HIGH",
                                "URGENT"
                        }
                );

        fields.add(priorityCombo);

        JLabel foodLabel =
                new JLabel(
                        "FOOD ITEM"
                );

        foodLabel.setFont(UI.BOLD);

        fields.add(foodLabel);

        itemCombo =
                new JComboBox<>();

        for (MenuItem item :
                system.menuService
                        .getMenu()) {

            itemCombo.addItem(item);
        }

        fields.add(itemCombo);

        JLabel quantityLabel =
                new JLabel(
                        "QUANTITY"
                );

        quantityLabel.setFont(UI.BOLD);

        fields.add(quantityLabel);

        quantitySpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                1,
                                1,
                                50,
                                1
                        )
                );

        fields.add(quantitySpinner);

        top.add(
                fields,
                BorderLayout.CENTER
        );

        add(
                top,
                BorderLayout.NORTH
        );


        JPanel tableCard =
                UI.card();

        orderModel =
                new DefaultTableModel(
                        new String[]{
                                "ITEM",
                                "STATION",
                                "PRICE",
                                "QUANTITY",
                                "TOTAL"
                        },
                        0
                ) {

                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        orderTable =
                new JTable(orderModel);

        UI.styleTable(orderTable);

        tableCard.add(
                new JScrollPane(orderTable),
                BorderLayout.CENTER
        );

        add(
                tableCard,
                BorderLayout.CENTER
        );


        JPanel bottom =
                new JPanel(
                        new BorderLayout(
                                15,
                                10
                        )
                );

        bottom.setOpaque(false);


        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        buttons.setOpaque(false);

        JButton add =
                UI.button(
                        "ADD ITEM",
                        UI.BLUE
                );

        add.addActionListener(
                e -> addItem()
        );

        JButton remove =
                UI.button(
                        "REMOVE ITEM",
                        UI.RED
                );

        remove.addActionListener(
                e -> removeItem()
        );

        JButton create =
                UI.button(
                        "CREATE ORDER",
                        UI.GREEN
                );

        create.addActionListener(
                e -> createOrder()
        );

        JButton clear =
                UI.button(
                        "CLEAR",
                        UI.NAVY
                );

        clear.addActionListener(
                e -> clearOrder()
        );

        buttons.add(add);
        buttons.add(remove);
        buttons.add(create);
        buttons.add(clear);

        bottom.add(
                buttons,
                BorderLayout.NORTH
        );


        JPanel bill =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                15,
                                0
                        )
                );

        bill.setOpaque(false);

        subtotalLabel =
                billLabel(
                        "Subtotal: Rs. 0.00"
                );

        taxLabel =
                billLabel(
                        "Tax (5%): Rs. 0.00"
                );

        totalLabel =
                billLabel(
                        "TOTAL: Rs. 0.00"
                );

        totalLabel.setForeground(
                UI.ORANGE_DARK
        );

        totalLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        bill.add(subtotalLabel);
        bill.add(taxLabel);
        bill.add(totalLabel);

        bottom.add(
                bill,
                BorderLayout.SOUTH
        );

        add(
                bottom,
                BorderLayout.SOUTH
        );
    }

    private JLabel billLabel(
            String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(UI.BOLD);
        label.setForeground(UI.TEXT);

        label.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                UI.BORDER
                        ),
                        new EmptyBorder(
                                10,
                                12,
                                10,
                                12
                        )
                )
        );

        label.setOpaque(true);
        label.setBackground(Color.WHITE);

        return label;
    }

    private void addItem() {

        MenuItem item =
                (MenuItem)
                        itemCombo
                                .getSelectedItem();

        int quantity =
                (Integer)
                        quantitySpinner
                                .getValue();

        if (item == null) {

            showError(
                    "Please select a food item."
            );

            return;
        }

        if (quantity <= 0) {

            showError(
                    "Quantity must be greater than zero."
            );

            return;
        }

        double total =
                item.getPrice() *
                        quantity;

        orderModel.addRow(
                new Object[]{
                        item.getName(),
                        item.getStation(),
                        String.format(
                                "Rs. %.2f",
                                item.getPrice()
                        ),
                        quantity,
                        String.format(
                                "Rs. %.2f",
                                total
                        )
                }
        );

        updateBill();
    }

    private void removeItem() {

        int row =
                orderTable
                        .getSelectedRow();

        if (row == -1) {

            showError(
                    "Select an item to remove."
            );

            return;
        }

        orderModel.removeRow(row);

        updateBill();
    }

    private void createOrder() {

        if (orderModel.getRowCount() == 0) {

            showError(
                    "Add at least one item."
            );

            return;
        }

        int table =
                (Integer)
                        tableCombo
                                .getSelectedItem();

        String priority =
                (String)
                        priorityCombo
                                .getSelectedItem();

        try {

            Order order =
                    system.orderService
                            .createOrder(
                                    table,
                                    priority
                            );

            for (int i = 0;
                 i < orderModel
                         .getRowCount();
                 i++) {

                String itemName =
                        orderModel
                                .getValueAt(
                                        i,
                                        0
                                )
                                .toString();

                int quantity =
                        Integer.parseInt(
                                orderModel
                                        .getValueAt(
                                                i,
                                                3
                                        )
                                        .toString()
                        );

                system.orderService
                        .addItem(
                                order,
                                itemName,
                                quantity
                        );
            }

            system.orderService
                    .save(order);

            system.kitchenCoordinator
                    .createTasks(order);

            showBill(order);

            JOptionPane.showMessageDialog(
                    this,
                    "Order #" +
                            order.getOrderId() +
                            " created successfully.\n\n" +
                            "Kitchen preparation tasks created.",
                    "Order Created",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearOrder();

        } catch (Exception e) {

            showError(
                    e.getMessage()
            );
        }
    }

    private void updateBill() {

        double subtotal = 0;

        for (int i = 0;
             i < orderModel
                     .getRowCount();
             i++) {

            MenuItem item =
                    system.menuService
                            .findItem(
                                    orderModel
                                            .getValueAt(
                                                    i,
                                                    0
                                            )
                                            .toString()
                            );

            int quantity =
                    Integer.parseInt(
                            orderModel
                                    .getValueAt(
                                            i,
                                            3
                                    )
                                    .toString()
                    );

            subtotal +=
                    item.getPrice() *
                            quantity;
        }

        double tax =
                system.billCalculator
                        .calculateTax(
                                subtotal
                        );

        double total =
                system.billCalculator
                        .calculateTotal(
                                subtotal
                        );

        subtotalLabel.setText(
                "Subtotal: Rs. " +
                        String.format(
                                "%.2f",
                                subtotal
                        )
        );

        taxLabel.setText(
                "Tax (5%): Rs. " +
                        String.format(
                                "%.2f",
                                tax
                        )
        );

        totalLabel.setText(
                "TOTAL: Rs. " +
                        String.format(
                                "%.2f",
                                total
                        )
        );
    }

    private void showBill(
            Order order) {

        double subtotal =
                order.getSubtotal();

        double tax =
                system.billCalculator
                        .calculateTax(
                                subtotal
                        );

        double total =
                system.billCalculator
                        .calculateTotal(
                                subtotal
                        );

        StringBuilder bill =
                new StringBuilder();

        bill.append(
                "================================\n"
        );

        bill.append(
                "       DYNAMIC RESTAURANT\n"
        );

        bill.append(
                "             BILL\n"
        );

        bill.append(
                "================================\n\n"
        );

        bill.append(
                "Order #: "
        ).append(
                order.getOrderId()
        ).append("\n");

        bill.append(
                "Table: "
        ).append(
                order.getTableNumber()
        ).append("\n");

        bill.append(
                "Priority: "
        ).append(
                order.getPriority()
        ).append("\n\n");

        for (OrderItem item :
                order.getItems()) {

            bill.append(
                    item.getMenuItem()
                            .getName()
            );

            bill.append(
                    " x "
            );

            bill.append(
                    item.getQuantity()
            );

            bill.append(
                    " = Rs. "
            );

            bill.append(
                    String.format(
                            "%.2f",
                            item.getTotal()
                    )
            );

            bill.append("\n");
        }

        bill.append(
                "\n--------------------------------\n"
        );

        bill.append(
                "Subtotal: Rs. "
        ).append(
                String.format(
                        "%.2f",
                        subtotal
                )
        ).append("\n");

        bill.append(
                "Tax (5%): Rs. "
        ).append(
                String.format(
                        "%.2f",
                        tax
                )
        ).append("\n");

        bill.append(
                "TOTAL: Rs. "
        ).append(
                String.format(
                        "%.2f",
                        total
                )
        );

        JOptionPane.showMessageDialog(
                this,
                bill.toString(),
                "Customer Bill",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void clearOrder() {

        orderModel.setRowCount(0);

        updateBill();
    }

    private void showError(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


/* ============================================================
 * KITCHEN PANEL
 * ============================================================
 */

class KitchenPanel extends JPanel {

    private RestaurantSystem system;
    private String userRole;

    private JComboBox<Integer> orderCombo;

    private JTable taskTable;

    private DefaultTableModel taskModel;

    private JLabel orderStatusLabel;

public KitchenPanel(RestaurantSystem system, String userRole) {
    this.system = system;
    this.userRole = userRole.toUpperCase();

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                UI.CREAM
        );

        buildUI();
    }

    private void buildUI() {

        JPanel top =
                UI.card();

        JPanel controls =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );


        JLabel select =
                new JLabel(
                        "ORDER:"
                );

        select.setFont(UI.BOLD);

        controls.add(select);

        orderCombo =
                new JComboBox<>();

        orderCombo.setPreferredSize(
                new Dimension(
                        130,
                        34
                )
        );

        controls.add(orderCombo);

        JButton refresh =
                UI.button(
                        "REFRESH",
                        UI.BLUE
                );

        refresh.addActionListener(
                e -> refreshOrders()
        );

        controls.add(refresh);

        JButton accept =
                UI.button(
                        "ACCEPT ORDER",
                        UI.GREEN
                );

        accept.addActionListener(
                e -> acceptOrder()
        );

        controls.add(accept);

        orderStatusLabel =
                new JLabel(
                        "STATUS: -"
                );

        orderStatusLabel.setFont(
                UI.BOLD
        );

        controls.add(
                orderStatusLabel
        );

        top.add(
                controls,
                BorderLayout.CENTER
        );

        add(
                top,
                BorderLayout.NORTH
        );


        JPanel tableCard =
                UI.card();

        taskModel =
                new DefaultTableModel(
                        new String[]{
                                "TASK ID",
                                "ITEM",
                                "STATION",
                                "STATUS",
                                "DEPENDENCY",
                                "DELAY"
                        },
                        0
                ) {

                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        taskTable =
                new JTable(taskModel);

        UI.styleTable(taskTable);

        tableCard.add(
                new JScrollPane(taskTable),
                BorderLayout.CENTER
        );

        add(
                tableCard,
                BorderLayout.CENTER
        );


        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        bottom.setOpaque(false);

 
        JButton start =
                UI.button(
                        "START TASK",
                        UI.BLUE
                );

        start.addActionListener(
                e -> startTask()
        );

        JButton complete =
                UI.button(
                        "COMPLETE TASK",
                        UI.GREEN
                );

        complete.addActionListener(
                e -> completeTask()
        );

        JButton delay =
                UI.button(
                        "ADD DELAY",
                        UI.YELLOW
                );

        delay.setForeground(Color.WHITE);

        delay.addActionListener(
                e -> addDelay()
        );

        JButton resume =
                UI.button(
                        "RESUME TASK",
                        UI.ORANGE
                );

        resume.addActionListener(
                e -> resumeTask()
        );

        JButton cancel =
                UI.button(
                        "CANCEL ORDER",
                        UI.RED
                );

        cancel.addActionListener(
                e -> cancelOrder()
        );

        bottom.add(start);
        bottom.add(complete);
        bottom.add(delay);
        bottom.add(resume);

        if (userRole.equalsIgnoreCase("ADMIN") ||
                userRole.equalsIgnoreCase("STAFF")) {
            bottom.add(cancel);
        }

        add(
                bottom,
                BorderLayout.SOUTH
        );

        refreshOrders();
    }

    private void refreshOrders() {

        Integer previous =
                (Integer)
                        orderCombo
                                .getSelectedItem();

        orderCombo.removeAllItems();

        for (Order order :
                system.orderRepository
                        .getAll()) {

            orderCombo.addItem(
                    order.getOrderId()
            );
        }

        if (previous != null) {

            orderCombo.setSelectedItem(
                    previous
            );
        }

        refreshTasks();
    }

    private Order getSelectedOrder() {

        Integer id =
                (Integer)
                        orderCombo
                                .getSelectedItem();

        if (id == null) {
            return null;
        }

        return system.orderRepository
                .findById(id);
    }

    private void refreshTasks() {

        taskModel.setRowCount(0);

        Order order =
                getSelectedOrder();

        if (order == null) {

            orderStatusLabel.setText(
                    "STATUS: -"
            );

            return;
        }

        orderStatusLabel.setText(
                "STATUS: " +
                        order.getStatus() +
                        "   |   PRIORITY: " +
                        order.getPriority()
        );

        for (PreparationTask task :
                order.getTasks()) {

            String dependency =
                    task.getDependencyTaskId()
                            == -1
                            ? "NONE"
                            : "#" +
                            task.getDependencyTaskId();

            String delay =
                    task.getDelayMinutes()
                            == 0
                            ? "NONE"
                            : task.getDelayMinutes()
                            + " MIN";

            taskModel.addRow(
                    new Object[]{
                            task.getTaskId(),
                            task.getItemName(),
                            task.getStationName(),
                            task.getStatus(),
                            dependency,
                            delay
                    }
            );
        }
    }

    private PreparationTask
    getSelectedTask() {

        Order order =
                getSelectedOrder();

        int row =
                taskTable
                        .getSelectedRow();

        if (order == null ||
                row == -1) {

            return null;
        }

        int taskId =
                Integer.parseInt(
                        taskModel
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );

        for (PreparationTask task :
                order.getTasks()) {

            if (task.getTaskId()
                    == taskId) {

                return task;
            }
        }

        return null;
    }

    private void acceptOrder() {

        Order order =
                getSelectedOrder();

        if (order == null) {

            error(
                    "Select an order."
            );

            return;
        }

        try {

            system.kitchenCoordinator
                    .acceptOrder(order);

            refreshTasks();

        } catch (Exception e) {

            error(
                    e.getMessage()
            );
        }
    }

private void startTask() {

    Order order = getSelectedOrder();

    PreparationTask task = getSelectedTask();

    if (order == null || task == null) {
        error("Select an order and task.");
        return;
    }

    if (order.getStatus().equalsIgnoreCase("CANCELLED") ||
            task.getStatus().equalsIgnoreCase("CANCELLED")) {
        error("Cancelled orders or tasks cannot be processed.");
        return;
    }

    String result =
            system.kitchenCoordinator.startTask(
                    order,
                    task.getTaskId()
            );

    JOptionPane.showMessageDialog(this, result);

    refreshTasks();
}

private void completeTask() {

    Order order = getSelectedOrder();

    PreparationTask task = getSelectedTask();

    if (order == null || task == null) {
        error("Select an order and task.");
        return;
    }

    if (order.getStatus().equalsIgnoreCase("CANCELLED") ||
            task.getStatus().equalsIgnoreCase("CANCELLED")) {
        error("Cancelled orders or tasks cannot be processed.");
        return;
    }

    String result =
            system.kitchenCoordinator.completeTask(
                    order,
                    task.getTaskId()
            );

    JOptionPane.showMessageDialog(this, result);

    refreshTasks();
}


 
private void addDelay() {

    Order order = getSelectedOrder();
    PreparationTask task = getSelectedTask();

    if (order == null || task == null) {
        error("Select an order and task.");
        return;
    }

    if (order.getStatus().equalsIgnoreCase("CANCELLED") ||
            task.getStatus().equalsIgnoreCase("CANCELLED")) {
        error("Cancelled orders or tasks cannot be processed.");
        return;
    }

    String minutesText = JOptionPane.showInputDialog(
            this,
            "Delay in minutes:"
    );

    if (minutesText == null) {
        return;
    }

    int minutes;

    try {
        minutes = Integer.parseInt(minutesText);

        if (minutes <= 0) {
            error("Enter a number greater than zero.");
            return;
        }

    } catch (NumberFormatException e) {
        error("Enter a valid number.");
        return;
    }

    String reason = JOptionPane.showInputDialog(
            this,
            "Reason for delay:"
    );

    if (reason == null) {
        return;
    }

    if (reason.trim().isEmpty()) {
        error("Please enter a reason for the delay.");
        return;
    }

    String result = system.delayManager.addDelay(
            order,
            task.getTaskId(),
            minutes,
            reason
    );

    JOptionPane.showMessageDialog(this, result);
    refreshTasks();
}

   
private void resumeTask() {

    Order order = getSelectedOrder();
    PreparationTask task = getSelectedTask();

    if (order == null || task == null) {
        error("Select an order and task.");
        return;
    }

    if (order.getStatus().equalsIgnoreCase("CANCELLED") ||
            task.getStatus().equalsIgnoreCase("CANCELLED")) {
        error("Cancelled orders or tasks cannot be processed.");
        return;
    }

    String result = system.delayManager.resumeTask(
            order,
            task.getTaskId()
    );

    JOptionPane.showMessageDialog(this, result);
    refreshTasks();
}

    private void error(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    
private void cancelOrder() {

    Order order = getSelectedOrder();

    if (order == null) {
        error("Please select an order.");
        return;
    }

    if (order.getStatus().equalsIgnoreCase("CANCELLED")) {
        error("This order is already cancelled.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this order?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    String reason = JOptionPane.showInputDialog(
            this,
            "Enter the reason for cancellation:"
    );

    if (reason == null) {
        return;
    }

    if (reason.trim().isEmpty()) {
        error("Please enter a reason.");
        return;
    }

    String result = system.cancellationManager.cancelOrder(
            order,
            reason
    );

    JOptionPane.showMessageDialog(this, result);
    refreshOrders();
}

}


/* ============================================================
 * ALL ORDERS PANEL
 * ============================================================
 */

class OrdersPanel extends JPanel {

    private RestaurantSystem system;

    private DefaultTableModel model;

    public OrdersPanel(
            RestaurantSystem system) {

        this.system = system;

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                UI.CREAM
        );

        buildUI();
    }

    private void buildUI() {

        JPanel card =
                UI.card();

        model =
                new DefaultTableModel(
                        new String[]{
                                "ORDER ID",
                                "TABLE",
                                "PRIORITY",
                                "STATUS",
                                "SUBTOTAL",
                                "TOTAL",
                                "ITEMS"
                        },
                        0
                ) {

                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        JTable table =
                new JTable(model);

        UI.styleTable(table);

        card.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        add(
                card,
                BorderLayout.CENTER
        );

        JButton refresh =
                UI.button(
                        "REFRESH ORDERS",
                        UI.BLUE
                );

        refresh.addActionListener(
                e -> refresh()
        );

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        bottom.setOpaque(false);

        bottom.add(refresh);

        add(
                bottom,
                BorderLayout.SOUTH
        );

        refresh();
    }

    private void refresh() {

        model.setRowCount(0);

        for (Order order :
                system.orderRepository
                        .getAll()) {

            double subtotal =
                    order.getSubtotal();

            double total =
                    system.billCalculator
                            .calculateTotal(
                                    subtotal
                            );

            model.addRow(
                    new Object[]{
                            "#" +
                                    order.getOrderId(),
                            "Table " +
                                    order.getTableNumber(),
                            order.getPriority(),
                            order.getStatus(),
                            String.format(
                                    "Rs. %.2f",
                                    subtotal
                            ),
                            String.format(
                                    "Rs. %.2f",
                                    total
                            ),
                            order.getItems()
                                    .size()
                    }
            );
        }
    }
}


/* ============================================================
 * MENU PANEL
 * ============================================================
 */

class MenuPanel extends JPanel {

    private RestaurantSystem system;

    public MenuPanel(
            RestaurantSystem system) {

        this.system = system;

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                UI.CREAM
        );

        buildUI();
    }

    private void buildUI() {

        JPanel card =
                UI.card();

        DefaultTableModel model =
                new DefaultTableModel(
                        new String[]{
                                "ID",
                                "ITEM",
                                "CATEGORY",
                                "PRICE",
                                "KITCHEN STATION",
                                "AVAILABILITY"
                        },
                        0
                ) {

                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        JTable table =
                new JTable(model);

        UI.styleTable(table);

        for (MenuItem item :
                system.menuService
                        .getMenu()) {

            model.addRow(
                    new Object[]{
                            item.getId(),
                            item.getName(),
                            item.getCategory(),
                            "Rs. " +
                                    String.format(
                                            "%.2f",
                                            item.getPrice()
                                    ),
                            item.getStation(),
                            item.isAvailable()
                                    ? "AVAILABLE"
                                    : "UNAVAILABLE"
                    }
            );
        }

        card.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        add(
                card,
                BorderLayout.CENTER
        );


        JPanel info =
                new JPanel(
                        new BorderLayout()
                );

        info.setBackground(
                Color.WHITE
        );

        info.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                UI.BORDER
                        ),
                        new EmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );

        JLabel text =
                new JLabel(
                        "Each menu item is automatically assigned to a kitchen station for coordinated preparation."
                );

        text.setFont(UI.NORMAL);
        text.setForeground(UI.MUTED);

        info.add(text);

        add(
                info,
                BorderLayout.SOUTH
        );
    }
}


/* ============================================================
 * NOTIFICATION PANEL
 * ============================================================
 */

class NotificationPanel extends JPanel {

    private RestaurantSystem system;

    private DefaultListModel<String> model;

    public NotificationPanel(
            RestaurantSystem system) {

        this.system = system;

        setLayout(
                new BorderLayout(
                        15,
                        15
                )
        );

        setBackground(
                UI.CREAM
        );

        buildUI();
    }

    private void buildUI() {

        JPanel card =
                UI.card();

        model =
                new DefaultListModel<>();

        JList<String> list =
                new JList<>(model);

        list.setFont(UI.NORMAL);

        list.setBackground(
                Color.WHITE
        );

        list.setFixedCellHeight(55);

        list.setBorder(
                new EmptyBorder(
                        5,
                        5,
                        5,
                        5
                )
        );

        card.add(
                new JScrollPane(list),
                BorderLayout.CENTER
        );

        add(
                card,
                BorderLayout.CENTER
        );

        JButton refresh =
                UI.button(
                        "REFRESH NOTIFICATIONS",
                        UI.BLUE
                );

        refresh.addActionListener(
                e -> refresh()
        );

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        bottom.setOpaque(false);

        bottom.add(refresh);

        add(
                bottom,
                BorderLayout.SOUTH
        );

        refresh();
    }

    private void refresh() {

        model.clear();

        for (String message :
                system.notification
                        .getMessages()) {

            model.addElement(message);
        }

        if (model.isEmpty()) {

            model.addElement(
                    "No notifications yet."
            );
        }
    }
}
