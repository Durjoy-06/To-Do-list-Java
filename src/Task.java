import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Task implements Comparable<Task> {
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate dueDate;
    private boolean completed;

    private boolean reminderShown = false;

    public Task(String title, String description, String category, String priority, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
        this.reminderShown = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // Added getter and setter for reminderShown
    public boolean isReminderShown() {
        return reminderShown;
    }

    public void setReminderShown(boolean reminderShown) {
        this.reminderShown = reminderShown;
    }

    // Added getDaysLeft() method
    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // Add setters if needed for your edit functionality:
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

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
