package ein.imageconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.im4java.core.ConvertCmd;

import ein.imageconverter.model.DirectoryItem;
import ein.imageconverter.model.FileData;
import ein.imageconverter.service.ConvertService;
import ein.imageconverter.service.ConvertServiceImpl;
import ein.imageconverter.ui.component.DirectoryViewerController;
import ein.imageconverter.ui.component.FileListController;

public class ImageConverter extends Application {
    private String imageMagickPath = null;

    private DirectoryViewerController controller;
    private FileListController fileListController;

    private ConvertService convertService = new ConvertServiceImpl();

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) {

        Properties property = new Properties();
        try (InputStream propertyStream = new FileInputStream("config.properties")) {
            property.load(propertyStream);

            imageMagickPath = property.getProperty("path.imageMagick");
            if (Files.notExists(Paths.get(imageMagickPath))) {
                imageMagickPath = null;
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {

            Parent root = FXMLLoader.load(getClass().getResource("ui/Index.fxml"));

            TextField imageMagickPathField = (TextField) root.lookup("#imageMagickPath");
            imageMagickPathField.setText(imageMagickPath);

            this.fileListController = new FileListController((TableView<FileData>) root.lookup("#fileList"));

            this.controller = new DirectoryViewerController((TreeView<DirectoryItem>) root.lookup("#directoryViewer"),
                    fileListController);

            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("select ImageMagick path");
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));

            Button directorySelectButton = (Button) root.lookup("#directorySelectButton");
            directorySelectButton.setOnAction((event) -> {
                File selectedDir = chooser.showDialog(primaryStage);

                if (!selectedDir.exists()) {
                    return;
                }

                try (OutputStream output = new FileOutputStream("config.properties")) {

                    // set the properties value
                    property.setProperty("path.imageMagick", selectedDir.getAbsolutePath());

                    // save properties to project root folder
                    property.store(output, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            final ProgressBar progress = (ProgressBar) root.lookup("#executeProgress");
            progress.setVisible(false);
            Button executeButton = (Button) root.lookup("#executeButton");
            executeButton.setOnAction((event) -> {
                progress.setVisible(true);

                ConvertCmd cmd = new ConvertCmd();
                cmd.setSearchPath(imageMagickPath);

                File tempDir = new File(System.getProperty("java.io.tmpdir"), "" + new Date().getTime());
                tempDir.mkdirs();

                executeButton.setDisable(true);
                final Task<String> task = new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        List<FileData> fileDataList = fileListController.getSelectedFileList();
                        int totalSize = fileDataList.size();

                        for (FileData fileData : fileDataList) {

                            convertService.convertArchive(fileData, tempDir, cmd);

                            updateProgress(fileDataList.indexOf(fileData) + 1, totalSize);
                        }

                        // delete(tempDir);

                        return "";
                    }
                };
                progress.progressProperty().bind(task.progressProperty());
                final ExecutorService exe = Executors
                        .newSingleThreadExecutor();
                exe.submit(task);
                task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, w -> {
                    exe.shutdown();
                    executeButton.setDisable(false);
                    progress.setVisible(false);
                });

            });

            // Scene の作成・登録
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
