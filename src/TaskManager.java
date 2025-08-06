import javax.swing.*;
import java.awt.*;

public class TaskManager extends JFrame {
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField titleField;
    private JTextField descriptionField;

    public TaskManager() {
        setTitle("📋 To-Do List App");
        setSize(550, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Inputs ---
        titleField = new JTextField(20);
        descriptionField = new JTextField(20);
        JButton addButton = new JButton("➕ Add Task");

        // --- Task List Model ---
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(taskList);

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Task Title:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(descriptionField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(addButton, gbc);

        // --- Add Panels to Frame ---
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Button Action ---
        addButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descriptionField.getText().trim();
            if (!title.isEmpty() && !desc.isEmpty()) {
                taskListModel.addElement(new Task(title, desc));
                titleField.setText("");
                descriptionField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both title and description.");
            }
        });

        setVisible(true);
    }
}
