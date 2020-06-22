package com.mcwcapsule.VJVM.cli;

import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.vm.VMOptions;
import lombok.var;
import org.apache.commons.cli.*;

import java.util.Arrays;

/**
 * The CLI interface
 */
public final class CLI {
    static Options options;

    static {
        options = new Options();
        options.addOption(Option.builder("cp").longOpt("classpath").hasArg().argName("path").numberOfArgs(1)
            .desc(String.format("specify the class path to search, multiple paths should be separated by '%s'",
                System.getProperty("path.separator")))
            .build());
        options.addOption(Option.builder("h").longOpt("help").desc("print help message").build());
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }
        var parser = new DefaultParser();
        try {
            var cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                printHelp();
                return;
            }
            if (cmd.getArgs().length == 0)
                throw new ParseException("Main class required.");
            VJVM.init(VMOptions.builder().userClassPath(cmd.getOptionValue("cp")).entryClass(cmd.getArgs()[0])
                .args(Arrays.copyOfRange(cmd.getArgs(), 1, cmd.getArgs().length)).build());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    static void printHelp() {
        var formatter = new HelpFormatter();
        formatter.printHelp("jvvm [options] class [args...]", "options:", options, "");
    }
}
