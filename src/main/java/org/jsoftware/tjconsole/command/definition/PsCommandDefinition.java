package org.jsoftware.tjconsole.command.definition;

import java.util.prefs.BackingStoreException;

import org.jsoftware.tjconsole.command.CommandAction;
import org.jsoftware.tjconsole.localjvm.JvmPid;
import org.jsoftware.tjconsole.localjvm.ProcessListManager;

/**
 * Command to display local java processes list
 *
 * @author szalik
 */
public class PsCommandDefinition extends AbstractCommandDefinition {
    private final ProcessListManager processListManager = new ProcessListManager();

    public PsCommandDefinition() throws BackingStoreException {
        super("Display local java processes list", "Display local java processes list", "ps", false);
    }

    @Override
    public CommandAction action(final String input) {
        return (ctx, output) -> processListManager.getLocalProcessList().stream().filter(JvmPid::isValid)
                                                  .forEachOrdered(jvmPid -> output.println("@|red " + jvmPid.getPid() + "|@ @|green "
                                                          + jvmPid.getCommand() + "|@"));
    }

}
