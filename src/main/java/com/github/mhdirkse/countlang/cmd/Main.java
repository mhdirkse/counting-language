package com.github.mhdirkse.countlang.cmd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.github.mhdirkse.countlang.ast.OutputStrategy;
import com.github.mhdirkse.countlang.tasks.ExecuteProgramTask;

public final class Main implements OutputStrategy {
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
        try {
            executeReader(new FileReader(inputFileName));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    void executeReader(final Reader reader) throws IOException {
        try {
            new ExecuteProgramTask(reader).run(this);
        }
        finally {
            reader.close();
        }
    }

    @Override
    public void output(String msg) {
        System.out.println(msg);
    }

    @Override
    public void error(String msg) {
        System.out.println("ERROR: " + msg);
    }
}
