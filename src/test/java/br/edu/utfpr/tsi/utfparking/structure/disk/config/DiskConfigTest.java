package br.edu.utfpr.tsi.utfparking.structure.disk.config;

import br.edu.utfpr.tsi.utfparking.structure.disk.properties.DiskProperties;
import br.edu.utfpr.tsi.utfparking.structure.exceptions.SaveAvatarException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiskConfigTest {

    @Autowired
    private DiskConfig diskConfig;

    @Autowired
    private DiskProperties diskProperties;

    @Order(0)
    @Test
    void shouldSaveAvatar() throws IOException {
        var resource = this.getClass().getResourceAsStream("/avatar/1.png");
        var multipartFile = new MockMultipartFile("file", resource);

        var avatar = diskConfig.saveAvatar(multipartFile, 10L);

        assertNotNull(avatar);
        assertEquals("10.png", avatar.getFilename());
        assertTrue(Files.exists(avatar.getFile().toPath()));

        Files.delete(avatar.getFile().toPath());
    }

    @Order(1)
    @Test
    void shouldReturnDefaultAvatar() {
        var avatar = diskConfig.loadAvatar(10L);

        assertNotNull(avatar);
        assertEquals("default.png", avatar.getFilename());
    }

    @Order(2)
    @Test
    void shouldReturnAvatar() throws IOException {
        var resource = this.getClass().getResourceAsStream("/avatar/1.png");
        var multipartFile = new MockMultipartFile("file", resource);

        diskConfig.saveAvatar(multipartFile, 10L);

        var avatar = diskConfig.loadAvatar(10L);

        assertNotNull(avatar);
        assertEquals("10.png", avatar.getFilename());
        assertTrue(Files.exists(avatar.getFile().toPath()));

        Files.delete(avatar.getFile().toPath());
    }

    @Order(3)
    @Test
    void shouldReturnException() {
        try {
            FileSystemUtils.deleteRecursively(Path.of(diskProperties.getPath()));
            var resource = this.getClass().getResourceAsStream("/avatar/1.png");
            var multipartFile = new MockMultipartFile("file", resource);

            assertThrows(SaveAvatarException.class, () -> diskConfig.saveAvatar(multipartFile,10L));

            resource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
