package ein.imageconverter.ui.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import ein.imageconverter.model.FileData;


public class FileListController {
    private TableView<FileData> fileList;

    public FileListController(TableView<FileData> tableView) {
        this.fileList = tableView;

        CheckBox allCheck = new CheckBox();

        TableColumn<FileData, Boolean> checkColumn = new TableColumn<>("");
        checkColumn.setGraphic(allCheck);
        tableView.getColumns().add(checkColumn);
        checkColumn.setSortable(false);
        checkColumn.setCellValueFactory(new PropertyValueFactory<FileData, Boolean>("checked"));

        checkColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkColumn));

        tableView.setEditable(true);


        TableColumn<FileData, String> fileNameColumn = new TableColumn<>("File Name");
        tableView.getColumns().add(fileNameColumn);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<FileData, String>("fileName"));

        TableColumn<FileData, String> fileTypeColumn = new TableColumn<>("Type");
        tableView.getColumns().add(fileTypeColumn);
        fileTypeColumn.setCellValueFactory(new PropertyValueFactory<FileData, String>("fileType"));
        TableColumn<FileData, Long> fileSizeColumn = new TableColumn<>("Size");
        tableView.getColumns().add(fileSizeColumn);
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<FileData, Long>("fileSize"));

        allCheck.setOnAction((actionEvent) -> {
            boolean checked = allCheck.isSelected();
            for (FileData fileData : fileList.getItems()) {
                fileData.checkedProperty().set(checked);
            }
        });

//        final ImageView imageView = (ImageView) tableView.getParent().lookup("#imageView");

        tableView.setOnMouseClicked((event) -> {
            if (event.getClickCount()>0) {
                TableViewSelectionModel<FileData> selectedModel = fileList.getSelectionModel();
                if (selectedModel != null && selectedModel.getSelectedItem() != null) {
                    System.out.println(selectedModel.getSelectedItem().getFilePath());
                    FileData selectedFile = selectedModel.getSelectedItem();
                    if (selectedFile.getFileName().endsWith(".pdf")) {
//                        try {
//                            PDDocument document = PDDocument.load(selectedFile.getFilePath());
//
//                            //ページのリストから最初の1ページを取得する
//                            PDPage page = (PDPage) document.getDocumentCatalog().getAllPages().get(0);
//
//                            //ページからリソースを取得し、全てのイメージを取得する。
//                            PDResources resources = page.getResources();
//                            Map<String, PDXObject> images = resources.getXObjects();
//                            if (images != null) {
//                                Iterator imageIter = images.keySet().iterator();
//                                while (imageIter.hasNext()) {
//                                    String key = (String) imageIter.next();
//                                    PDXObjectImage image = (PDXObjectImage) images.get(key);
//                                    PDRectangle cropBox = page.findCropBox();
//                                    if (cropBox != null) {
//                                        //dpiの算出(pdfのデフォルトdpiは72)
//                                        int dpi = Math.round(image.getHeight() * 72 / cropBox.getHeight());
//                                        //dpiのサイズに変換しつつイメージ取得
//                                        BufferedImage bufferedImage = page.convertToImage(BufferedImage.TYPE_3BYTE_BGR, dpi);
//
//                                        WritableImage wr = null;
//                                        if (bufferedImage != null) {
//                                            wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
//                                            PixelWriter pw = wr.getPixelWriter();
//                                            for (int x = 0; x < bufferedImage.getWidth(); x++) {
//                                                for (int y = 0; y < bufferedImage.getHeight(); y++) {
//                                                    pw.setArgb(x, y, bufferedImage.getRGB(x, y));
//                                                }
//                                            }
//                                        }
//
////                                        Image image = new Image()
////                                        imageView.setImage(wr);
////                                        File outputfile = new File(System.currentTimeMillis() + "_hoge.jpg");
////                                        ImageIO.write(bufferedImage, "jpg", outputfile);
//                                    }
//                                }
//                            }
//                            document.close();
//                        } catch (IOException e) {
//                            // TODO 自動生成された catch ブロック
//                            e.printStackTrace();
//                        }
                    }
                }


            }
        });


    }

    public void updateFileList(String path) {
        fileList.getItems().clear();

        File dir = new File(path);
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }

        for (File child : children) {
            String fileName = child.getName();
            if (fileName != null && (fileName.endsWith(".zip") || fileName.endsWith(".pdf"))) {
                fileList.getItems().add(new FileData(fileName, child.getAbsolutePath(), "zip", child.length()));
            }
        }
    }

    public List<FileData> getSelectedFileList() {
        List<FileData> result = new ArrayList<>();
        for (FileData fileData : fileList.getItems()) {
            if (fileData.getChecked()) {
                result.add(fileData);
            }
        }

        return result;
    }
}
