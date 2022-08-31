import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LineInfo {

    private String text;
    private int value;
    private List<LineInfo> lineList;
    private int position;

    public LineInfo(String text) {
        this.text = text;
        lineList = new ArrayList<>();
    }

    public void addListToLineList(LineInfo lineInfo) {
        lineList.add(lineInfo);
    }
}
