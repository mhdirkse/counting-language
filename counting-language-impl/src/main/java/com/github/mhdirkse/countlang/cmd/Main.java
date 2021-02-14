/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.cmd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.tasks.ProgramExecutor;

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
            new ProgramExecutor(reader).run(this);
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
