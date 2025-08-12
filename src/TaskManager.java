import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskManager extends JFrame {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField titleField, descriptionField, dueDateField;
    private JComboBox<String> categoryBox, priorityBox;
    private List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();

        setTitle("📋 Smart To-Do List");
        setSize(680, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(Color.decode("#f5f5f5"));
        add(panel);

        // Input Form (top)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(panel.getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = new JTextField(20);
        descriptionField = new JTextField(30);
        categoryBox = new JComboBox<>(new String[]{"Work", "Study", "Personal"});
        priorityBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        dueDateField = new JTextField(LocalDate.now().format(DATE_FMT)); // yyyy-MM-dd

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(titleField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; inputPanel.add(categoryBox, gbc);

        // Row 1
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(descriptionField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; inputPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; inputPanel.add(priorityBox, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Due Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(dueDateField, gbc);

        JButton addButton = new JButton("➕ Add Task");
        addButton.setBackground(Color.decode("#4CAF50"));
        addButton.setForeground(Color.white);
        addButton.setFocusPainted(false);
        gbc.gridx = 3; gbc.gridy = 2; inputPanel.add(addButton, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Center list
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setCellRenderer(new TaskCellRenderer());
        JScrollPane scroll = new JScrollPane(taskList);
        panel.add(scroll, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        bottom.setBackground(panel.getBackground());
        JButton markDoneBtn = new JButton("✔ Mark Done");
        JButton deleteBtn = new JButton("🗑 Delete");

        bottom.add(markDoneBtn);
        bottom.add(deleteBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        // Add action
        addButton.addActionListener(e -> addTask());
        markDoneBtn.addActionListener(e -> markTaskDone());
        deleteBtn.addActionListener(e -> deleteTask());

        // Double click to edit
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && taskList.getSelectedValue() != null) {
                    editTask(taskList.getSelectedValue());
                }
            }
        });

        // Right-click menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem mDone = new JMenuItem("Mark Done");
        JMenuItem mDelete = new JMenuItem("Delete");
        mDone.addActionListener(ev -> markTaskDone());
        mDelete.addActionListener(ev -> deleteTask());
        popup.add(mDone);
        popup.add(mDelete);
        taskList.setComponentPopupMenu(popup);

     

        // Reminder timer: runs every 60 seconds
        Timer reminderTimer = new Timer(60_000, e -> checkReminders());
        reminderTimer.setInitialDelay(2_000);
        reminderTimer.start();

        // immediate reminder check at startup
        SwingUtilities.invokeLater(this::checkReminders);

        setVisible(true);
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String desc = descriptionField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String priority = (String) priorityBox.getSelectedItem();
        String dateText = dueDateField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title");
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateText, DATE_FMT);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd");
            return;
        }

        Task t = new Task(title, desc, category, priority, date);
        tasks.add(t);
        Collections.sort(tasks);
        refreshList();

        // immediate small reminder if due within 2 days
        long days = t.getDaysLeft();
        if (!t.isCompleted() && days >= 0 && days <= 2 && !t.isReminderShown()) {
            JOptionPane.showMessageDialog(this, "⏰ Reminder: \"" + t.getTitle() + "\" is due in " + days + " day(s).");
            t.setReminderShown(true);
        }

        titleField.setText("");
        descriptionField.setText("");
        dueDateField.setText(LocalDate.now().format(DATE_FMT));
    }

    private void markTaskDone() {
        Task sel = taskList.getSelectedValue();
        if (sel != null) {
            sel.setCompleted(true);
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to mark done.");
        }
    }

    private void deleteTask() {
        Task sel = taskList.getSelectedValue();
        if (sel != null) {
            int opt = JOptionPane.showConfirmDialog(this, "Delete selected task?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                tasks.remove(sel);
                refreshList();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to delete.");
        }
    }

    private void editTask(Task t) {
        // small edit dialog panel
        JTextField titleFld = new JTextField(t.getTitle(), 20);
        JTextField descFld = new JTextField(t.getDescription(), 20);
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Work", "Study", "Personal"});
        catBox.setSelectedItem(t.getCategory());
        JComboBox<String> priBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        priBox.setSelectedItem(t.getPriority());
        JTextField dateFld = new JTextField(t.getDueDate().format(DATE_FMT), 10);

        JPanel p = new JPanel(new GridLayout(5, 2, 6, 6));
        p.add(new JLabel("Title:")); p.add(titleFld);
        p.add(new JLabel("Description:")); p.add(descFld);
        p.add(new JLabel("Category:")); p.add(catBox);
        p.add(new JLabel("Priority:")); p.add(priBox);
        p.add(new JLabel("Due Date (yyyy-MM-dd):")); p.add(dateFld);

        int res = JOptionPane.showConfirmDialog(this, p, "Edit Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalDate newDate = LocalDate.parse(dateFld.getText().trim(), DATE_FMT);
                t.setTitle(titleFld.getText().trim());
                t.setDescription(descFld.getText().trim());
                t.setCategory((String) catBox.getSelectedItem());
                t.setPriority((String) priBox.getSelectedItem());
                t.setDueDate(newDate);
                t.setReminderShown(false); // allow reminder again if changed
                Collections.sort(tasks);
                refreshList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Edit cancelled.");
            }
        }
    }

    private void refreshList() {
        Collections.sort(tasks);
        taskListModel.clear();
        for (Task t : tasks) taskListModel.addElement(t);
    }

    // check reminders: find tasks due today or tomorrow and not shown yet
    private void checkReminders() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        StringBuilder sb = new StringBuilder();
        for (Task t : tasks) {
            if (!t.isCompleted() && !t.isReminderShown()) {
                if (t.getDueDate().isEqual(today) || t.getDueDate().isEqual(tomorrow)) {
                    sb.append("• ").append(t.getTitle()).append(" (due ").append(t.getDueDate()).append(")\n");
                    t.setReminderShown(true); // show once
                }
            }
        }
        if (sb.length() > 0) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "⏰ Upcoming tasks:\n" + sb.toString(), "Reminders", JOptionPane.INFORMATION_MESSAGE));
        }
    }

    // custom renderer with color coding and overdue highlight
    private class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (!(value instanceof Task)) return label;
            Task t = (Task) value;

            String priorityColor;
            switch (t.getPriority().toLowerCase()) {
                case "high": priorityColor = "#c9302c"; break; // red
                case "medium": priorityColor = "#f0ad4e"; break; // orange
                default: priorityColor = "#5cb85c"; // green
            }

            String daysText = (t.getDaysLeft() < 0) ? "<span style='color:#ffffff; background:#d9534f; padding:2px;'> Overdue </span>"
                    : "<span style='color:gray;'> " + t.getDaysLeft() + " days left</span>";

            String titleEsc = escapeHtml(t.getTitle());
            String descEsc = escapeHtml(t.getDescription());

            String html = "<html><div style='font-family:Segoe UI;'>" +
                    "<b>" + titleEsc + "</b> <small style='color:#666;'>(" + t.getCategory() + ")</small><br/>" +
                    "<span style='color:" + priorityColor + ";'>" + t.getPriority() + "</span> — " +
                    descEsc + " — due <b>" + t.getDueDate() + "</b> " + daysText +
                    "</div></html>";

            label.setText(html);

            // styling selection & completed
            if (t.isCompleted()) {
                label.setForeground(Color.GRAY);
            }

            // for selection background ensure opaque
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(new Color(100, 149, 237));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
            }

            return label;
        }

        // small HTML escape helper
        private String escapeHtml(String s) {
            if (s == null) return "";
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
