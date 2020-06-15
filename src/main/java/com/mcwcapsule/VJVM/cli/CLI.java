package com.mcwcapsule.VJVM.cli;

import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;

import lombok.*;

/**
 * The CLI interface
 */
public final class CLI {
    static Options options;

    static {
        options = new Options();
        options.addOption(Option.builder("cp").longOpt("classpath").hasArg().argName("path")
                .desc(String.format("specify the class path to search, multiple paths should be separated by '%s'",
                        System.getProperty("path.separator")))
                .build());
        options.addOption(Option.builder("h").longOpt("help").desc("print help message").build());
    }

    /**
     * Says hello to the world.
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
            // TODO: call class loader and interpreter
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    static void printHelp() {
        var formatter = new HelpFormatter();
        formatter.printHelp("jvvm [options] class [args...]", "options:", options, "");
    }
}
