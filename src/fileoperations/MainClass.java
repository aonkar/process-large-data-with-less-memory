package fileoperations;

import fileoperations.sorts.MinHeap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static fileoperations.constants.FileConstants.CHUNK_SIZE;
import static fileoperations.constants.FileConstants.INPUT_FILE;
import static fileoperations.constants.FileConstants.OUTPUT_FILE;
import static fileoperations.utils.FileUtil.deFragmentInputToMultipleFiles;
import static fileoperations.utils.FileUtil.getFileLines;
import static fileoperations.utils.FileUtil.publishDataToFinalOutput;
import static fileoperations.utils.FileUtil.reloadDataFromFile;

/**
 * The type Main class.
 */
public class MainClass {

    /**
     * Main.
     *
     * @param args the args
     * @throws IOException the io exception
     */
    public static void main(final String[] args) throws IOException {
        System.out.println(getFileLines(OUTPUT_FILE));
        final MinHeap minHeap;
        final BufferedWriter finalOutputBufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE, true));
        List<BufferedReader> outputFilesBufferedReader = null;
        try {
            long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            final long start = System.currentTimeMillis();
            final int totalDeFragmentedFiles = deFragmentInputToMultipleFiles(INPUT_FILE);
            outputFilesBufferedReader = new ArrayList<>(totalDeFragmentedFiles);
            for (int fileNumber = 1; fileNumber <= totalDeFragmentedFiles; fileNumber++) {
                outputFilesBufferedReader.add(new BufferedReader(new FileReader("output_" + fileNumber + ".txt")));
            }
            final int processingChunkSize = Math.toIntExact(CHUNK_SIZE / (totalDeFragmentedFiles));
            minHeap = new MinHeap(processingChunkSize);
            for (int fileNumber = 1; fileNumber <= totalDeFragmentedFiles; fileNumber++) {
                reloadDataFromFile(fileNumber, outputFilesBufferedReader, processingChunkSize, minHeap);
            }
            final int[] fileElementsCount = new int[totalDeFragmentedFiles];
            while (isMoreDataRead(outputFilesBufferedReader)) {
                final int fileNumberToReload = publishDataToFinalOutput(processingChunkSize,
                        false,
                        fileElementsCount,
                        finalOutputBufferedWriter,
                        minHeap);
                System.out.println("fileNumberToReload: " + fileNumberToReload);
                reloadDataFromFile(fileNumberToReload, outputFilesBufferedReader, processingChunkSize, minHeap);
            }
            while (!minHeap.isEmpty()) {
                finalOutputBufferedWriter.write(minHeap.removeElement());
                finalOutputBufferedWriter.newLine();
            }
            final long end = System.currentTimeMillis();
            long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.out.println("Total time taken:" + Duration.ofMillis(end - start).toSeconds());
            System.out.println("Total memory used:" + (afterUsedMem - beforeUsedMem));
            System.out.println("Done.");
        } finally {
            try {
                finalOutputBufferedWriter.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if (outputFilesBufferedReader != null) {
                outputFilesBufferedReader.parallelStream().forEach(bufferedReader -> {
                    try {
                        bufferedReader.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
            }
            System.out.println("Final file lines: " + getFileLines(OUTPUT_FILE));
        }
    }

    private static boolean isMoreDataRead(final List<BufferedReader> outputFilesBufferedReader) {
        return outputFilesBufferedReader.parallelStream().anyMatch(bufferedReader -> {
            try {
                return bufferedReader.ready();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return false;
        });
    }
}