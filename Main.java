package mini_proj_DBMS;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class Main extends JFrame {
    static final String DB_URL = "jdbc:mysql://localhost:3306/arcade_exchange_db";
    static final String USER = "root";
    static final String PASS = "Itslate@12";
    Connection conn;

    public Main() {
        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!");
            System.exit(1);
        }

        // Set up main frame
        setTitle("Arcade Exchange Management");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(0,0,0));

        // Header label
        JLabel headerLabel = new JLabel("The Arcade Exchange", JLabel.CENTER);
        headerLabel.setFont(new Font("Press Start 2P", Font.BOLD, 28)); // Retro-style font if available
        headerLabel.setForeground(new Color(0, 255, 255)); // Neon cyan color for text
        headerLabel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Adding a glowing effect (neon glow around the text)
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(0, 0, 0)); // Black background
        headerLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2)); // Cyan border to match the glow

        // Add a hover effect for interactivity (if needed, or you can use this directly for static glow effect)
        headerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                headerLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 20, 147), 3)); // Neon Pink Glow
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                headerLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2)); // Reset to Neon Cyan border
            }
        });

        add(headerLabel, BorderLayout.NORTH);


        // Button panel setup
        JPanel buttonPanel = new JPanel(new GridLayout(7, 4, 15, 15));
        buttonPanel.setBackground(new Color(0,0,0));
        buttonPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create buttons with styles
        JButton insertCustomerBtn = createStyledButton("Insert Customer");
        insertCustomerBtn.addActionListener(new InsertCustomerListener());
        buttonPanel.add(insertCustomerBtn);

        JButton viewCustomersBtn = createStyledButton("Show Customers");
        viewCustomersBtn.addActionListener(new ViewCustomersListener());
        buttonPanel.add(viewCustomersBtn);

        JButton insertGameBtn = createStyledButton("Insert Game");
        insertGameBtn.addActionListener(new InsertGameListener());
        buttonPanel.add(insertGameBtn);

        JButton viewGamesBtn = createStyledButton("Show Games");
        viewGamesBtn.addActionListener(new ViewGamesListener());
        buttonPanel.add(viewGamesBtn);

        JButton insertPurchaseBtn = createStyledButton("Insert Purchase");
        insertPurchaseBtn.addActionListener(new InsertPurchaseListener());
        buttonPanel.add(insertPurchaseBtn);

        JButton viewPurchasesBtn = createStyledButton("Show Purchases");
        viewPurchasesBtn.addActionListener(new ViewPurchasesListener());
        buttonPanel.add(viewPurchasesBtn);

        JButton insertReviewBtn = createStyledButton("Insert Review");
        insertReviewBtn.addActionListener(new InsertReviewListener());
        buttonPanel.add(insertReviewBtn);
        
        JButton viewReviewsBtn = createStyledButton("View Reviews");
        viewReviewsBtn.addActionListener(new ViewReviewsListener());
        buttonPanel.add(viewReviewsBtn);

        JButton viewTransactionsBtn = createStyledButton("View Transactions");
        viewTransactionsBtn.addActionListener(new ViewTransactionsListener());
        buttonPanel.add(viewTransactionsBtn);

        // New button for generating a bill
        JButton generateBillBtn = createStyledButton("Generate Bill");
        generateBillBtn.addActionListener(new GenerateBillListener());
        buttonPanel.add(generateBillBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    // Method to create styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Century Gothic", Font.BOLD, 20));
        button.setBackground(new Color(0, 0, 0));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(57, 255, 20), 2));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect for neon glow
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 3));  // Cyan Glow
                button.setBackground(new Color(0, 255, 255));  // Neon Cyan background when hovered
            }
            
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(57, 255, 20), 2));  // Neon Green border
                button.setBackground(new Color(0, 0, 0));  // Return to black background
            }
        });

        return button;
    }


    // Listener to handle generating a bill for a customer
    class GenerateBillListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String customerIdStr = JOptionPane.showInputDialog("Enter Customer ID:");
            if (customerIdStr == null) return;
            
            try {
                int customerId = Integer.parseInt(customerIdStr);

                // Retrieve customer information
                String customerQuery = "SELECT * FROM customer WHERE customer_id = ?";
                PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
                customerStmt.setInt(1, customerId);
                ResultSet customerRs = customerStmt.executeQuery();

                if (!customerRs.next()) {
                    JOptionPane.showMessageDialog(null, "Customer not found.");
                    return;
                }

                String customerName = customerRs.getString("customer_name");
                String email = customerRs.getString("email");
                String phone = customerRs.getString("phone_number");

                // Retrieve purchase details for the customer
                String purchaseQuery = "SELECT games.game_id, games.original_price, purchase.purchase_price " +
                        "FROM purchase JOIN games ON purchase.game_id = games.game_id " +
                        "WHERE purchase.customer_id = ?";
                PreparedStatement purchaseStmt = conn.prepareStatement(purchaseQuery);
                purchaseStmt.setInt(1, customerId);
                ResultSet purchaseRs = purchaseStmt.executeQuery();

                // Format the bill
                StringBuilder bill = new StringBuilder();
                bill.append("Bill for Customer ID: ").append(customerId).append("\n");
                bill.append("Name: ").append(customerName).append("\n");
                bill.append("Email: ").append(email).append("\n");
                bill.append("Phone: ").append(phone).append("\n\n");
                bill.append("Purchases:\n");
                bill.append("Game ID\tOriginal Price\tPurchase Price\n");

                double totalAmount = 0;
                while (purchaseRs.next()) {
                    int gameId = purchaseRs.getInt("game_id");
                    double originalPrice = purchaseRs.getDouble("original_price");
                    double purchasePrice = purchaseRs.getDouble("purchase_price");
                    totalAmount += purchasePrice;

                    bill.append(gameId).append("\t")
                        .append(originalPrice).append("\t")
                        .append(purchasePrice).append("\n");
                }

                bill.append("\nTotal Amount: ").append(totalAmount);
                
                // Display the bill
                JOptionPane.showMessageDialog(null, new JTextArea(bill.toString()), "Customer Bill", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating bill.");
            }
        }
    }
    class InsertCustomerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog("Enter Customer Name:");
            String email = JOptionPane.showInputDialog("Enter Customer Email:");
            String phone = JOptionPane.showInputDialog("Enter Customer Phone:");

            if (name != null && email != null && phone != null) {
                try {
                    String sql = "INSERT INTO customer (customer_name, email, phone_number) VALUES (?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    pstmt.setString(2, email);
                    pstmt.setString(3, phone);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Customer inserted successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error inserting customer.");
                }
            }
        }
    }

    class InsertGameListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String priceStr = JOptionPane.showInputDialog("Enter Game Price:");
            try {
                double price = Double.parseDouble(priceStr);
                String sql = "INSERT INTO games (original_price, purchase_count, real_time_price) VALUES (?, 0, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, price);
                pstmt.setDouble(2, price);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Game inserted successfully.");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inserting game.");
            }
        }
    }

    class InsertPurchaseListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String customerIdStr = JOptionPane.showInputDialog("Enter Customer ID:");
            String gameIdStr = JOptionPane.showInputDialog("Enter Game ID:");

            try {
                int customerId = Integer.parseInt(customerIdStr);
                int gameId = Integer.parseInt(gameIdStr);

                String priceQuery = "SELECT real_time_price FROM games WHERE game_id = ?";
                PreparedStatement priceStmt = conn.prepareStatement(priceQuery);
                priceStmt.setInt(1, gameId);
                ResultSet rs = priceStmt.executeQuery();
                double realTimePrice = 0.0;
                if (rs.next()) {
                    realTimePrice = rs.getDouble("real_time_price");
                }
                rs.close();

                String sql = "INSERT INTO purchase (customer_id, game_id, purchase_price) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, customerId);
                pstmt.setInt(2, gameId);
                pstmt.setDouble(3, realTimePrice);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Purchase inserted successfully.");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inserting purchase.");
            }
        }
    }

    class InsertReviewListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String customerIdStr = JOptionPane.showInputDialog("Enter Customer ID:");
            String gameIdStr = JOptionPane.showInputDialog("Enter Game ID:");
            String ratingStr = JOptionPane.showInputDialog("Enter Rating (1-5):");
            String review = JOptionPane.showInputDialog("Enter Review:");

            try {
                int customerId = Integer.parseInt(customerIdStr);
                int gameId = Integer.parseInt(gameIdStr);
                int rating = Integer.parseInt(ratingStr);

                if (rating < 1 || rating > 5) {
                    JOptionPane.showMessageDialog(null, "Rating should be between 1 and 5.");
                    return;
                }

                String sql = "INSERT INTO review (customer_id, game_id, rating, review) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, customerId);
                pstmt.setInt(2, gameId);
                pstmt.setInt(3, rating);
                pstmt.setString(4, review);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Review inserted successfully.");
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error inserting review.");
            }
        }
    }

    class ViewReviewsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM review", "Reviews");
        }
    }

    class ViewCustomersListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM customer", "Customers");
        }
    }

    class ViewGamesListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM games", "Games");
        }
    }

    class ViewPurchasesListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM purchase", "Purchases");
        }
    }

    // Newly added ViewTransactionsListener to display transactions
    class ViewTransactionsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            displayTableData("SELECT * FROM transaction", "Transactions");
        }
    }

    // Utility method to display table data
    private void displayTableData(String query, String title) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names to model
            Vector<String> columns = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }
            model.setColumnIdentifiers(columns);

            // Add rows to model

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                model.addRow(row);
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);

            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying " + title);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainApp = new Main();
            mainApp.setVisible(true);
        });
    }
}