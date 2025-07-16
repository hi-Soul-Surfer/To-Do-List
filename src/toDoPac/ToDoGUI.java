package toDoPac;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToDoGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final List<Task> tasks = new ArrayList<>();
    private static final String FILE_NAME = "tasks_gui.txt";

    public ToDoGUI() {
        setTitle("To-Do List");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top input panel
        JTextField titleField = new JTextField(15);
        JTextField dueField = new JTextField(10);
        JComboBox<String> priorityBox = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"});

        JButton addBtn = new JButton("Add Task");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Due Date (yyyy-mm-dd):"));
        inputPanel.add(dueField);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(priorityBox);
        inputPanel.add(addBtn);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Completed", "Title", "Due Date", "Priority"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom panel
        JButton deleteBtn = new JButton("Delete Selected");
        JButton saveBtn = new JButton("Save Tasks");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(deleteBtn);
        bottomPanel.add(saveBtn);

        // Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load and refresh
        loadTasks();
        refreshTable();

        // Add button
        addBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String dueText = dueField.getText().trim();
            Priority priority = Priority.fromString((String) priorityBox.getSelectedItem());

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task title cannot be empty!");
                return;
            }

            LocalDate dueDate = null;
            if (!dueText.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueText);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format!");
                    return;
                }
            }

            Task task = new Task(title, dueDate, priority);
            tasks.add(task);
            refreshTable();

            titleField.setText("");
            dueField.setText("");
        });

        // Delete button
        deleteBtn.addActionListener(e -> {
            int[] selected = table.getSelectedRows();
            if (selected.length == 0) return;

            for (int i = selected.length - 1; i >= 0; i--) {
                tasks.remove(selected[i]);
            }
            refreshTable();
        });

        // Save button
        saveBtn.addActionListener(e -> {
            saveTasks();
            JOptionPane.showMessageDialog(this, "Tasks saved!");
        });

        // Checkbox handler
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                    int row = e.getFirstRow();
                    boolean completed = (Boolean) tableModel.getValueAt(row, 0);
                    tasks.get(row).markCompleted(completed);
                }
            }
        });    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                    task.isCompleted(),
                    task.getTitle(),
                    task.getDueDate() != null ? task.getDueDate().toString() : "None",
                    task.getPriority().toString()
            });
        }
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.println(task.toFileString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks.");
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = Task.fromFileString(line);
                if (task != null) tasks.add(task);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoGUI().setVisible(true));
    }
}
