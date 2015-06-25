package ein.imageconverter.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileData {

    private BooleanProperty checked;

    private StringProperty fileName;
    private StringProperty filePath;
    private StringProperty fileType;
    private LongProperty fileSize;

    public FileData(String fileName, String filePath, String fileType, long fileSize) {
        this.checked = new SimpleBooleanProperty(false);

        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.fileType = new SimpleStringProperty(fileType);
        this.fileSize = new SimpleLongProperty(fileSize);
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public void setChecked(boolean value) {
        checked.set(value);
    }

    public boolean getChecked() {
        return checked.get();
    }

    public String getFileName() {
        return fileName.get();
    }

    public String getFilePath() {
        return filePath.get();
    }

    public Long getFileSize() {
        return fileSize.get();
    }

    public String getFileType() {
        return fileType.get();
    }
}
