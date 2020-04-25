package br.edu.utfpr.tsi.utfparking.domain.users.avatar.service;

import br.edu.utfpr.tsi.utfparking.structure.disk.config.DiskConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileService {

    private final DiskConfig diskConfig;

    public Resource saveDisk(MultipartFile file, Long id) {
        return diskConfig.saveAvatar(file, id);
    }

    public Resource getFile(Long id) {
        return diskConfig.loadAvatar(id);
    }

}
