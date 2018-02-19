package logging;

public enum Level {
    INFO("INFO"), WARNING("WARNING"), SEVERE("SEVERE");

    private String prefix;

    private Level(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return prefix;
    }
}