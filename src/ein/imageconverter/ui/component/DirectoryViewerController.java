package ein.imageconverter.ui.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ein.imageconverter.model.DirectoryItem;
import ein.imageconverter.model.DirectoryTypeEnum;

public class DirectoryViewerController {

    private FileListController fileListController;

    public DirectoryViewerController(TreeView<DirectoryItem> treeView, FileListController fileListController) {
        this.fileListController = fileListController;

        try {
            Image driveIcon = new Image(new FileInputStream(new File(
                            "resources/icon/Hard Disk/Hard Disk_16x16.png")));
            Image directoryIcon = new Image(new FileInputStream(new File(
                            "resources/icon/Folder/Folder_16x16.png")));

            File[] roots = File.listRoots();

            DirectoryItem item = new DirectoryItem("Root",
                            DirectoryTypeEnum.DISC);
            TreeItem<DirectoryItem> rootItem = new TreeItem<DirectoryItem>(
                            item, new ImageView(driveIcon));
            rootItem.addEventHandler(TreeItem.<DirectoryItem>branchExpandedEvent(), new EventHandler<TreeItem.TreeModificationEvent<DirectoryItem>>() {
                @Override
                public void handle(TreeModificationEvent<DirectoryItem> event) {
                    TreeItem<DirectoryItem> item = event.getTreeItem();
                    System.out.println("open?" + item.getValue());

                    for (TreeItem<DirectoryItem> children : item.getChildren()) {

                        File dir = new File(children.getValue().getDirectoryPath());
                        File[] dirs = dir.listFiles();
                        if (dirs != null) {
                            for (File file : dirs) {
                                if (file.isDirectory()) {
                                    TreeItem<DirectoryItem> child = new TreeItem<DirectoryItem>(new DirectoryItem(file.getName(), file.getAbsolutePath()),
                                                    new ImageView(directoryIcon));
                                    children.getChildren().add(child);


                                }
                            }
                        }
                    }
                }
            });

            rootItem.setExpanded(true);

            int count = 0;

            for (File drive : roots) {
                TreeItem<DirectoryItem> driveItem = new TreeItem<DirectoryItem>(
                                new DirectoryItem(drive.getPath(), drive.getPath()), new ImageView(driveIcon));

                if (drive.listFiles() != null) {
                    for (File file : drive.listFiles()) {
                        if (file.isDirectory()) {
                            TreeItem<DirectoryItem> child = new TreeItem<DirectoryItem>(new DirectoryItem(file.getName(), file.getAbsolutePath()),
                                            new ImageView(directoryIcon));
                            driveItem.getChildren().add(child);
                        }
                    }
                }
                ++count;
                rootItem.getChildren().add(driveItem);
            }

            // for (int i = 1; i < 6; i++) {
            // TreeItem<String> item = new TreeItem<String> ("Message" + i);
            // rootItem.getChildren().add(item);
            // }
            treeView.setRoot(rootItem);
            treeView.setShowRoot(false);

            treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    TreeItem<DirectoryItem> item = treeView.getSelectionModel().getSelectedItem();
                    if (item == null) {
                        return;
                    }

                    System.out.println("Selected Text : " + item.getValue());

                    DirectoryItem selectedDir = item.getValue();

                    fileListController.updateFileList(selectedDir.getDirectoryPath());

                }
            });
        } catch (FileNotFoundException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

    }
}
