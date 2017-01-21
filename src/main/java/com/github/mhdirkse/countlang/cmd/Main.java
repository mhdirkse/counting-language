package com.github.mhdirkse.countlang.cmd;

import java.io.File;

public final class Main {
    public static void main(String[] args)
    {
        new Main(args).run();
    }

    private String[] args = null;
    private String inputFileName = null;

    private Main(final String[] args)
    {
        this.args = args;
    }

    private void run()
    {
        if(checkNumberOfArguments())
        {
            inputFileName = args[0];
            if(checkInputFileExists())
            {
                executeFile(inputFileName);
            }
        }
    }

    private boolean checkNumberOfArguments() {
        if(args.length != 1)
        {
            error("Invalid number of arguments");
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean checkInputFileExists() {
        File inputFile = new File(inputFileName);
        if (inputFile.exists()) {
            return true;
        }
        else
        {
            error("File does not exist: " + inputFile.getAbsolutePath());
            return false;
        }
    }

    private void executeFile(final String inputFileName) {
        // Temporary implementation
        System.out.println("OK");
    }

    private void error(String msg) {
        System.out.println(msg);
    }
}
