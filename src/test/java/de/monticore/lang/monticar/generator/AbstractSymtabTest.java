/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.monticar.generator;

import de.monticore.lang.monticar.generator.order.simulator.AbstractSymtab;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

/**
 * Common methods for symboltable tests
 */
public class AbstractSymtabTest extends AbstractSymtab {
    public static void testFilesAreEqual(List<File> files, String restPath) {
        for (File f : files) {
            File fileTarget = new File("./src/test/resources/results/" + restPath + f.getName());
            if(f.getName().equals("Helper.h") || f.getName().equals("HelperA.h")){
                continue;
            }
            Log.debug("" + fileTarget.exists(), "Exists");
            Log.debug(f.getName() + " " + fileTarget.getName(), "Comparing");
            assertTrue(areBothFilesEqual(f, fileTarget));
        }
    }

    public static void checkFilesAreEqual(List<File> generatedFiles, Path generatedFilesBasePath, String resultsPath) {
        for (File f : generatedFiles) {
            Path relativePath = generatedFilesBasePath.relativize(f.toPath());
            File fileTarget = new File("./src/test/resources/results/" + resultsPath + relativePath.toString());
            assertTrue(areBothFilesEqual(f, fileTarget));
        }
    }

    public static boolean areBothFilesEqual(File file1, File file2) {
        if (!file1.exists()) {
            Assert.fail("file does not exist: " + file1.getAbsolutePath());
            return false;
        }
        if (!file2.exists()) {
            Assert.fail("file does not exist: " + file2.getAbsolutePath());
            return false;
        }
        List<String> lines1;
        List<String> lines2;
        try {
            lines1 = Files.readAllLines(file1.toPath());
            lines2 = Files.readAllLines(file2.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IO error: " + e.getMessage());
            return false;
        }
        lines1 = discardEmptyLines(lines1);
        lines2 = discardEmptyLines(lines2);
        if (lines1.size() != lines2.size()) {
            Assert.fail(
                    "files have different number of lines: "
                            + file1.getAbsolutePath()
                            + " has " + lines1
                            + " lines and " + file2.getAbsolutePath() + " has " + lines2 + " lines"
            );
            return false;
        }
        int len = lines1.size();
        for (int i = 0; i < len; i++) {
            String l1 = lines1.get(i);
            String l2 = lines2.get(i);
            Assert.assertEquals("files differ in " + i + " line: "
                            + file1.getAbsolutePath()
                            + " has " + l1
                            + " and " + file2.getAbsolutePath() + " has " + l2,
                    l1,
                    l2
            );
        }
        return true;
    }

    private static List<String> discardEmptyLines(List<String> lines) {
        return lines.stream()
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .collect(Collectors.toList());
    }
}
