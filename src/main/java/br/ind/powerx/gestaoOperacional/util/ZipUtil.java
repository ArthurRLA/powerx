package br.ind.powerx.gestaoOperacional.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.InputStreamResource;

import br.ind.powerx.gestaoOperacional.services.order.definition.GeneratedFile;

public class ZipUtil {

	public static InputStreamResource createZip(List<GeneratedFile> files) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            
            byte[] buffer = new byte[8192];

            for (GeneratedFile file : files) {
                ZipEntry zipEntry = new ZipEntry(file.getFilename());
                zos.putNextEntry(zipEntry);

                try (InputStream inputStream = file.getResource().getInputStream()) {
                    int length;
                    while ((length = inputStream.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                }
                
                zos.closeEntry();
            }
            
            zos.finish();
            
            return new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
        }
    }
}
