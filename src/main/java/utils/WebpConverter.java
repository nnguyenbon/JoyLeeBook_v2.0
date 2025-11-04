package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.UUID;

public class WebpConverter {

    public static String convertToWebp(Part filePart, ServletContext context) throws IOException {
        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
            throw new IOException("File name is empty");
        }

        // 🔹 Tạo tên file ngẫu nhiên, tránh trùng
        String baseName = submittedFileName
                .replaceAll("[^a-zA-Z0-9._-]", "_") // chỉ giữ ký tự hợp lệ
                .replaceFirst("[.][^.]+$", ""); // bỏ phần mở rộng cũ

        String uniqueID = UUID.randomUUID().toString().substring(0, 8);
        String webpFileName = baseName + "-" + uniqueID + ".webp";

        // ✅ Xác định thư mục gốc project thật sự (kể cả khi Tomcat chạy ở bin/)
        File projectDir = new File(WebpConverter.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath());

        while (projectDir != null && !new File(projectDir, "src").exists()) {
            projectDir = projectDir.getParentFile();
        }

        if (projectDir == null) {
            throw new IOException("Cannot locate project root folder!");
        }
        String projectUploadPath = new File(projectDir, "src/main/webapp/img").getAbsolutePath();
        String targetUploadPath = context.getRealPath("/img"); // luôn trỏ đến folder deploy thực tế

        // 🔹 Thư mục lưu chính trong project
        File projectUploadDir = new File(projectUploadPath);
        if (!projectUploadDir.exists()) projectUploadDir.mkdirs();

        // 🔹 Thư mục lưu thêm trong target để render ngay
        File targetUploadDir = new File(targetUploadPath);
        if (!targetUploadDir.exists()) targetUploadDir.mkdirs();

        // 🔹 Đọc & chuyển đổi ảnh
        BufferedImage originalImage;
        try (InputStream input = filePart.getInputStream()) {
            originalImage = ImageIO.read(input);
        }

        if (originalImage == null) {
            throw new IOException("Unsupported or corrupted image format!");
        }

        BufferedImage resized = Thumbnails.of(originalImage)
                .size(190, 250)
                .outputQuality(0.8f)
                .asBufferedImage();

        // 🔹 Ghi ra 2 nơi (src và target)
        File projectOutput = new File(projectUploadDir, webpFileName);
        File targetOutput = new File(targetUploadDir, webpFileName);

        boolean success = false;
        if (ImageIO.getImageWritersByFormatName("webp").hasNext()) {
            success = ImageIO.write(resized, "webp", projectOutput);
            ImageIO.write(resized, "webp", targetOutput);
        } else {
            // fallback -> PNG nếu không có writer WebP
            success = ImageIO.write(resized, "png", projectOutput);
            ImageIO.write(resized, "png", targetOutput);
        }

        if (!success) {
            throw new IOException("Failed to write image (no suitable writer found)");
        }

        // 🔹 Ghi log kiểm tra
        System.out.println("✅ Image saved at:");
        System.out.println("   - " + projectOutput.getAbsolutePath());
        System.out.println("   - " + targetOutput.getAbsolutePath());

        return webpFileName;
    }
}
