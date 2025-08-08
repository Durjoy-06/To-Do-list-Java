import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class TaskManager extends JFrame {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField titleField, descriptionField;
    private JComboBox<String> categoryBox, priorityBox;
    private JFormattedTextField dueDateField;
    private java.util.List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
        setTitle("📋 Smart To-Do List");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.decode("#f5f5f5"));
        add(panel);

        // Input Form
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 8, 8));
        inputPanel.setBackground(Color.decode("#f5f5f5"));

        titleField = new JTextField();
        descriptionField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Work", "Study", "Personal"});
        priorityBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        dueDateField = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dueDateField.setValue(LocalDate.now());

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);

        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(priorityBox);

        inputPanel.add(new JLabel("Due Date (yyyy-MM-dd):"));
        inputPanel.add(dueDateField);

        JButton addButton = new JButton("➕ Add Task");
        addButton.setBackground(Color.decode("#4CAF50"));
        addButton.setForeground(Color.white);
        addButton.addActionListener(e -> addTask());
        inputPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Task List
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setCellRenderer(new TaskRenderer());
        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton markDoneBtn = new JButton("✔ Mark Done");
        JButton deleteBtn = new JButton("🗑 Delete");

        markDoneBtn.addActionListener(e -> markTaskDone());
        deleteBtn.addActionListener(e -> deleteTask());

        buttonPanel.add(markDoneBtn);
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String desc = descriptionField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String priority = (String) priorityBox.getSelectedItem();
        LocalDate date = LocalDate.parse(dueDateField.getText());

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a title");
            return;
        }

        tasks.add(new Task(title, desc, category, priority, date));
        Collections.sort(tasks);
        refreshList();
        titleField.setText("");
        descriptionField.setText("");
    }

    private void markTaskDone() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            selected.setCompleted(true);
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to mark done.");
        }
    }

    private void deleteTask() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            tasks.remove(selected);
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Select a task to delete.");
        }
    }

    private void refreshList() {
        taskListModel.clear();
        for (Task t : tasks) {
            taskListModel.addElement(t);
        }
    }

    // Custom Renderer to highlight overdue tasks
    private class TaskRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Task) {
                Task task = (Task) value;
                if (task.getDueDate().isBefore(LocalDate.now()) && !task.isCompleted()) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                if (task.isCompleted()) {
                    c.setForeground(Color.GRAY);
                }
            }
            return c;
        }
    }
}
