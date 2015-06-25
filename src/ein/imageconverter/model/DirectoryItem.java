package ein.imageconverter.model;

import java.io.File;

public class DirectoryItem {

    private String directoryName;
    private String directoryPath;
    private DirectoryTypeEnum directoryType;

    public DirectoryItem(File directory, DirectoryTypeEnum type) {
        directoryName = directory.getName();
        directoryPath = directory.getAbsolutePath();
        directoryType = type;
    }

    public DirectoryItem(String name, DirectoryTypeEnum type) {
        directoryName = name;
        directoryType = type;
    }

    public DirectoryItem(String name, String path) {
        directoryName = name;
        directoryPath = path;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public DirectoryTypeEnum getDirectoryType() {
        return directoryType;
    }

    public void setDirectoryType(DirectoryTypeEnum directoryType) {
        this.directoryType = directoryType;
    }

    @Override
    public String toString() {
        return getDirectoryName();
    }

}
