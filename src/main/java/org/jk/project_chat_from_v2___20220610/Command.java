package org.jk.project_chat_from_v2___20220610;


public enum Command {

    VIEW_ROOMS("//vr"),
    SWITCH_ROOM("//sr"),
    VIEW_HISTORY("//vh"),
    VIEW_FILES("//vf"),
    SEND_FILE("//sf"),
    DOWNLOAD_FILE("//df"),
    UNKNOWN("");

    private final String operation;

    Command(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }

    public static Command getByOperation(String operation) {

        for (Command c : Command.values()) {
            if (c.getOperation().equals(operation)) {
                return c;
            }
        }

        return UNKNOWN;
    }

}
