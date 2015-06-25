package ein.imageconverter.service;

import java.io.File;

import org.im4java.core.ConvertCmd;

import ein.imageconverter.model.FileData;

public interface ConvertService {

    void convertArchive(FileData fileData, File tempDir, ConvertCmd cmd);

}
