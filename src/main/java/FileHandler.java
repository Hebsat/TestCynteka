import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class FileHandler {

    private final String inputPath;
    private final String outputPath;
    private final StringJoiner result;

    public FileHandler(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        result = new StringJoiner(System.lineSeparator());
    }

    public void handleFile() throws IOException {
        LinesLists linesLists = getLinesLists(readFile());
        if (linesLists != null) {
            getEqualsList(linesLists).forEach(l -> result.add(l));
        }
        Logger.getLogger(FileHandler.class.getName()).info(result.toString());
        writeToFile();
    }

    private List<String> readFile() throws IOException {
        return Files.readAllLines(Path.of(inputPath));
    }

    private void writeToFile() throws IOException {
        Files.write(Path.of(outputPath), result.toString().getBytes());
    }

    private LinesLists getLinesLists(List<String> lines) {
        Logger.getLogger(FileHandler.class.getName()).info(lines.toString());
        int firstListCount;
        int secondListCount;
        try {
            firstListCount = Integer.parseInt(lines.get(0));
            secondListCount = Integer.parseInt(lines.get(firstListCount + 1));
        }
        catch (NumberFormatException e) {
            result.add("incorrect numbers format");
            return null;
        }
        catch (IndexOutOfBoundsException e) {
            result.add("incorrect first number ");
            return null;
        }
        if (lines.size() < firstListCount + secondListCount + 2) {
            result.add("the number of lines is less than stated");
            return null;
        }
        List<String> firstList = new ArrayList<>(lines.subList(1, firstListCount + 1));
        List<String> secondList = new ArrayList<>(lines.subList(firstListCount + 2, firstListCount + secondListCount + 2));
        return new LinesLists(firstList, secondList);
    }

    private List<String> getEqualsList(LinesLists linesLists) {
        List<String> result = new ArrayList<>();
        if (linesLists.getFirstList().size() == 1 && linesLists.getSecondList().size() == 1) {
            result.add(lineCombiner(linesLists.getFirstList().get(0), linesLists.getSecondList().get(0)));
            return result;
        }
        List<LineInfo> firstListLines = new ArrayList<>();
        List<String> secondListLines = new ArrayList<>(linesLists.getSecondList());
        linesLists.getFirstList().forEach(line1l -> {
            LineInfo currentLine = getEqualsValues(line1l, linesLists.getSecondList());
            currentLine.setPosition(firstListLines.size());
            firstListLines.add(currentLine);
        });
        firstListLines.sort(Comparator.comparing(lineInfo ->
                lineInfo.getLineList().stream().max(Comparator.comparing(LineInfo::getValue)).get().getValue()));
        Collections.reverse(firstListLines);
        firstListLines.forEach(lineInfo -> {
            String secondListLine = lineInfo.getLineList().stream().max(Comparator.comparing(LineInfo::getValue)).get().getText();
            if (secondListLines.stream().anyMatch(secondListLine::equals)) {
                lineInfo.setText(lineCombiner(lineInfo.getText(), secondListLine));
                secondListLines.remove(secondListLines.stream().filter(secondListLine::equals).findFirst().get());
            } else {
                lineInfo.setText(lineCombiner(lineInfo.getText(), "?"));
            }
        });
        firstListLines.sort(Comparator.comparing(LineInfo::getPosition));
        firstListLines.forEach(lineInfo -> result.add(lineInfo.getText()));
        if (secondListLines.size() > 0) {
            secondListLines.forEach(line -> result.add(lineCombiner(line, "?")));
        }
        return result;
    }

    private LineInfo getEqualsValues(String firstLine, List<String> lineList) {
        LineInfo lineInfo = new LineInfo(firstLine);
        lineList.forEach(secondLine -> {
            LineInfo currentLine = new LineInfo(secondLine);
            int value = firstLine.matches(secondLine) ? Integer.MAX_VALUE : getEqualsCountOfTwoLines(firstLine, secondLine);
            currentLine.setValue(value);
            lineInfo.addListToLineList(currentLine);
        });
        return lineInfo;
    }

    private int getEqualsCountOfTwoLines(String firstLine, String secondLine) {
        AtomicInteger equalsCount = new AtomicInteger();
        Arrays.stream(getWordsInLine(firstLine)).forEach(word1l ->
            Arrays.stream(getWordsInLine(secondLine)).forEach(word2l -> {
                if (wordsEquals(word1l.toLowerCase(), word2l.toLowerCase())) {
                    equalsCount.getAndIncrement();
                }
            }));
        return equalsCount.intValue();
    }

    private String[] getWordsInLine(String line) {
        return line.split(" ");
    }

    private boolean wordsEquals(String word1, String word2) {
        if (word1.matches(word2)) {
            return true;
        }
        String simpleWord1 = word1.length() > 1 ? word1.substring(0, word1.length() - 2) : word1.substring(0, word1.length() - 1);
        String simpleWord2 = word2.length() > 1 ? word2.substring(0, word2.length() - 2) : word2.substring(0, word2.length() - 1);
        return (word1.startsWith(simpleWord2) || word2.startsWith(simpleWord1)) && !(simpleWord1.isEmpty() || simpleWord2.isEmpty());
    }

    private String lineCombiner(String line1, String line2) {
        return line1.concat(":").concat(line2);
    }
}
