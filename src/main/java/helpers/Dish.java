package helpers;

public class Dish {
    private final int id;

    public Dish(int id) {
        this.id = id;
        Utils.printVerbose("Dish", "Created: " + id);
    }

    public Dish(Long id) {
        this.id = id.intValue();
        Utils.printVerbose("Dish", "Created: " + id);
    }

    public String toString() {
        return String.valueOf(id);
    }
}
