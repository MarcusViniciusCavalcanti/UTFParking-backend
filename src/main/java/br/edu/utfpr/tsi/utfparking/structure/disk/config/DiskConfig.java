package br.edu.utfpr.tsi.utfparking.structure.disk.config;

import br.edu.utfpr.tsi.utfparking.structure.disk.properties.DiskProperties;
import br.edu.utfpr.tsi.utfparking.structure.exceptions.SaveAvatarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiskConfig {

    private final DiskProperties diskProperties;

    private final ResourceLoader resourceLoader;

    public Resource saveAvatar(MultipartFile file, Long id) {
        var path = Path.of(diskProperties.getPath() + File.separator + id + ".png");

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return new UrlResource(path.normalize().toUri());
        } catch (IOException e) {
            log.error("Error ao salvar avatar", e);
            throw new SaveAvatarException("Problemas na tentativa de salvar arquivo.", e);
        }
    }

    public Resource loadAvatar(Long id) {
        var path = Path.of(diskProperties.getPath() + File.separator + id + ".png");

        try {
            if (Files.exists(path)) {
                var normalize = path.normalize();
                return new UrlResource(normalize.toUri());
            } else {
                return resourceLoader.getResource("classpath:image/default.png");
            }
        } catch (MalformedURLException e) {
            log.error("Error ao carregar avatar", e);
        }

        return null;
    }
}
