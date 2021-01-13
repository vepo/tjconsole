package org.jsoftware.tjconsole.command.definition;

import java.util.stream.Stream;

import org.jsoftware.tjconsole.command.CommandAction;

public class ListCommandDefinition extends AbstractCommandDefinition {

    public ListCommandDefinition() {
        super("List all registered beans", "List all registered beans", "list", false);
    }

    @Override
    public CommandAction action(String input) {
        return (tjContext, output) -> Stream.of(tjContext.getServer()
                                                         .getDomains())
                                            .forEachOrdered(output::println);
    }

}
