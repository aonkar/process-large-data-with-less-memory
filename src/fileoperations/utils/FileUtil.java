package fileoperations.utils;

import fileoperations.sorts.MergeSort;
import fileoperations.sorts.MinHeap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static fileoperations.constants.FileConstants.CHUNK_SIZE;
import static fileoperations.constants.FileConstants.DELIMITER;
import static fileoperations.constants.FileConstants.EXCEPTION;
import static fileoperations.constants.FileConstants.OUTPUT;
import static fileoperations.constants.FileConstants.OUTPUT_EXTENSION;

/**
 * The type File util.
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * De fragment input to multiple files int.
     *
     * @param filename the filename
     * @return the int number of de-fragmented files
     */
    public static int deFragmentInputToMultipleFiles(final String filename) {
        int fileNumberCount = 1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            while (bufferedReader.read() > 0) {
                final String[] names = bufferedReader.lines().limit(CHUNK_SIZE).toArray(String[]::new);
                final String[] sortedNames = MergeSort.mergeSort(names);
                try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT + fileNumberCount++ +
                        OUTPUT_EXTENSION, true))) {
                    int lineCount = 0;
                    for (final String name : sortedNames) {
                        lineCount++;
                        bufferedWriter.write(name);
                        if (lineCount != CHUNK_SIZE) {
                            bufferedWriter.newLine();
                        }
                    }
                } catch (IOException exception) {
                    System.err.println(EXCEPTION + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            System.err.println(EXCEPTION + exception.getMessage());
        }
        return fileNumberCount - 1;
    }

    /**
     * Reload data from file.
     *
     * @param fileNumber                the file number
     * @param outputFilesBufferedReader the output files buffered reader
     * @param processingChunkSize       the processing chunk size
     * @param minHeap                   the min heap
     */
    public static void reloadDataFromFile(final int fileNumber,
                                          final List<BufferedReader> outputFilesBufferedReader,
                                          final long processingChunkSize,
                                          final MinHeap minHeap) {
        outputFilesBufferedReader.get(fileNumber - 1)
                .lines()
                .limit(processingChunkSize)
                .map(line -> fileNumber + DELIMITER + line)
                .forEach(minHeap::addElement);
    }

    /**
     * Gets file lines.
     *
     * @param fileName the file name
     * @return the file lines
     */
    public static long getFileLines(final String fileName) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            return bufferedReader.lines().parallel().count();
        } catch (IOException exception) {
            System.out.println(EXCEPTION + exception.getMessage());
        }
        return 0;
    }

    /**
     * Publish data to final output int.
     *
     * @param processingChunkSize       the processing chunk size
     * @param reload                    the reload
     * @param fileElementsCount         the file elements count
     * @param finalOutputBufferedWriter the final output buffered writer
     * @param minHeap                   the min heap
     * @return the int
     * @throws IOException the io exception
     */
    public static int publishDataToFinalOutput(final long processingChunkSize,
                                               boolean reload,
                                               final int[] fileElementsCount,
                                               final BufferedWriter finalOutputBufferedWriter,
                                               final MinHeap minHeap) throws IOException {
        int fileNumberToReload = 0;
        while (!reload && !minHeap.isEmpty()) {
            final String removedElement = minHeap.removeElement();
            final String[] fileNoWithName = removedElement.split(DELIMITER);
            try {
                finalOutputBufferedWriter.write(fileNoWithName[1]);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            finalOutputBufferedWriter.newLine();
            final int fileNumber = Integer.parseInt(fileNoWithName[0]) - 1;
            fileElementsCount[fileNumber]++;
            if (fileElementsCount[fileNumber] == processingChunkSize) {
                reload = true;
                fileNumberToReload = fileNumber + 1;
                fileElementsCount[fileNumber] = 0;
            }
        }
        return fileNumberToReload;
    }
}
