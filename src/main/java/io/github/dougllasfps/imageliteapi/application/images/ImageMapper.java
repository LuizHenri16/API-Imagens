package io.github.dougllasfps.imageliteapi.application.images;

import io.github.dougllasfps.imageliteapi.domain.entity.Image;
import io.github.dougllasfps.imageliteapi.domain.enums.ImageExtension;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class ImageMapper {

    public Image mapToImage(MultipartFile file, String name, List<String> tags) throws IOException {
        return  Image.builder()
                .name(name) // Define o nome do arquivo
                .size(file.getSize()) // Define o tamnho do arquivo
                .tags(String.join(",", tags)) // Usando o Join do String é possível criar uma string de uma array usando um separador, para definir as tags
                .extension(ImageExtension.valueOf(MediaType.valueOf(file.getContentType()))) // Definir como extensão o valor do enum com o
                .file(file.getBytes())         //Definir os bytes do arquivo                // metodo criado para filtrar o tipo recebido do arquivo
                .build();
    }
}
