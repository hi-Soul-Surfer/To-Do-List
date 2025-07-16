package toDoPac;

import java.time.LocalDate;

public class Task {
    private String title;
    private boolean completed;
    private LocalDate dueDate;
    private Priority priority;

    public Task(String title, LocalDate dueDate, Priority priority) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted(boolean status) {
        this.completed = status;
    }

    public String toFileString() {
        return completed + ";" + title + ";" + (dueDate != null ? dueDate : "null") + ";" + priority;
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split(";", 4);
        if (parts.length < 4) return null;

        boolean completed = Boolean.parseBoolean(parts[0]);
        String title = parts[1];
        LocalDate dueDate = "null".equals(parts[2]) ? null : LocalDate.parse(parts[2]);
        Priority priority = Priority.fromString(parts[3]);

        Task task = new Task(title, dueDate, priority);
        task.markCompleted(completed);
        return task;
    }
}
