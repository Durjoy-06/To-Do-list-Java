import java.time.LocalDate;

public class Task implements Comparable<Task> {
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate dueDate;
    private boolean completed;

    public Task(String title, String description, String category, String priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // Sorting by priority first, then due date
    @Override
    public int compareTo(Task other) {
        int priorityCompare = Integer.compare(priorityValue(this.priority), priorityValue(other.priority));
        if (priorityCompare != 0) return priorityCompare;
        return this.dueDate.compareTo(other.dueDate);
    }

    private int priorityValue(String p) {
        switch (p.toLowerCase()) {
            case "high": return 1;
            case "medium": return 2;
            default: return 3;
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s) — %s — due %s",
                title, category, priority, dueDate);
    }
}
