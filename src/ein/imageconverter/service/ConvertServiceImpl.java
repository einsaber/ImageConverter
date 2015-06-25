package ein.imageconverter.service;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import ein.imageconverter.model.FileData;

public class ConvertServiceImpl implements ConvertService {

    @Override
    public void convertArchive(FileData fileData, File tempDir, ConvertCmd cmd) {
        // TODO 自動生成されたメソッド・スタブ
        File tempZipDir = new File(tempDir.getAbsolutePath() + File.separator + fileData.getFileName());
        tempZipDir.mkdirs();

        boolean isResize = false;
        float ratio = 1.0f;

        try {
            ZipFile zf = new ZipFile(new File(fileData.getFilePath()), Charset.forName("SJIS"));
            for (Enumeration<? extends ZipEntry> e = zf.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();

                File outFile = new File(tempZipDir.getAbsolutePath() + File.separator + entry.getName());

                if (entry.isDirectory())
                    outFile.mkdir();
                else {
                    BufferedInputStream in = null;
                    BufferedOutputStream out = null;

                    try {
                        // 圧縮ファイル入力ストリーム作成
                        in = new BufferedInputStream(zf.getInputStream(entry));

                        // 親ディレクトリがない場合、ディレクトリ作成
                        if (!outFile.getParentFile().exists())
                            outFile.getParentFile().mkdirs();

                        // 出力オブジェクト取得
                        out = new BufferedOutputStream(new FileOutputStream(outFile));

                        // 読み込みバッファ作成
                        byte[] buffer = new byte[1024 * 1024 * 16];

                        // 解凍ファイル出力
                        int readSize = 0;
                        while ((readSize = in.read(buffer)) != -1) {
                            out.write(buffer, 0, readSize);
                        }
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    }

                    String outFileName = outFile.getName().toLowerCase();
                    if (outFileName.endsWith("jpg")
                            || outFileName.endsWith("jpeg")) {
                        BufferedImage bimg = ImageIO.read(outFile);
                        int width = bimg.getWidth();
                        int height = bimg.getHeight();

                        int limitHeight = 1500;
                        if (height > limitHeight) {
                            ratio = (float) limitHeight / (float) height;
                        }

                        if (isResize || ratio < 1.0f) {
                            IMOperation op = new IMOperation();
                            op.addImage(outFile.getAbsolutePath());
                            op.resize((int) (width * ratio), (int) (height * ratio));
                            System.out.println(outFile.getName() + ", ratio : " + ratio);
                            op.addImage(outFile.getAbsolutePath() + ".resized");
                            cmd.run(op);
                            outFile.delete();
                            Files.move(Paths.get(outFile.getAbsolutePath() + ".resized"), Paths.get(outFile.getAbsolutePath()));

                            isResize = true;
                        }
                    }
                }
            }
            File file = new File(tempDir.getAbsolutePath() + File.separator + fileData.getFileName() + ".zip");
            File[] files = tempZipDir.listFiles();
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file), Charset.forName("SJIS"));
            // zos.setLevel(5);
            try {
                byte[] buffer = new byte[1024 * 1024 * 16];
                compress(zos, files, tempZipDir.getAbsolutePath(), buffer);
            } finally {
                zos.flush();
                zos.close();
            }

            zf.close();
        } catch (IOException | InterruptedException | IM4JavaException e) {
            e.printStackTrace();
        }
    }

    private void compress(ZipOutputStream zos, File[] files, String rootPath,
            byte[] buffer) throws IOException {

        for (File f : files) {
            String entryPath = f.getAbsolutePath().replace(rootPath + File.separator, "").replace(File.separator, "/");
            if (f.isDirectory()) {
                final ZipEntry entry = new ZipEntry(entryPath + "/");
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(0);
                entry.setCrc(0);
                // zos.putNextEntry(entry);
                // zos.closeEntry();

                compress(zos, f.listFiles(), rootPath, buffer);
            } else {
                System.out.println(entryPath);
                ZipEntry entry = new ZipEntry(entryPath);
                // entry.setLastModifiedTime(FileTime.fromMillis(new
                // Date().getTime()));

                // entry.setMethod(ZipEntry.STORED);
                entry.setLastModifiedTime(FileTime.fromMillis(f.lastModified()));
                entry.setCreationTime(FileTime.fromMillis(f.lastModified()));
                // entry.setSize(f.length());
                zos.putNextEntry(entry);
                try (InputStream is = new BufferedInputStream(
                        new FileInputStream(f))) {
                    for (;;) {
                        int len = is.read(buffer);
                        if (len < 0)
                            break;
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
        }
    }

    private void delete(File f) {
        if (f == null || !f.exists()) {
            return;
        }
        if (f.isFile()) {
            // ファイル削除
            if (f.exists() && !f.delete()) {
                System.out.println("cannot delete2 : " + f.getAbsolutePath());
                f.deleteOnExit();
            }
        } else {
            // ディレクトリの場合、再帰する
            File[] list = f.listFiles();
            for (int i = 0; i < list.length; i++) {
                delete(list[i]);
            }
            if (f.exists() && !f.delete()) {
                System.out.println("cannot delete : " + f.getAbsolutePath());
                f.deleteOnExit();
            }
        }
    }
}
