public class Task {
    private String title;
    private String description;
    private boolean isDone;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        isDone = true;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return (isDone ? "[✔] " : "[ ] ") + title + " - " + description;
    }

    public String toFileString() {
        return title + "::" + description + "::" + isDone;
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split("::");
        if (parts.length == 3) {
            Task task = new Task(parts[0], parts[1]);
            if (Boolean.parseBoolean(parts[2])) task.markAsDone();
            return task;
        }
        return null;
    }
}
