package io.github.dougllasfps.imageliteapi.application.images;

import io.github.dougllasfps.imageliteapi.domain.entity.Image;
import io.github.dougllasfps.imageliteapi.domain.enums.ImageExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor // para
@Slf4j
public class ImagesController {

    private final ImageServiceImpl imageService;
    private final ImageMapper imageMapper;

    @PostMapping
    public ResponseEntity save(
            //Criando uma API para salvar imagem, aqui é recebido uma imagem do Front usando o Multipart do Spring para poder receber
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("tags") List<String> tags
            ) throws IOException {

        log.info("Arquivo recebido > name: {}, size: {}", file.getOriginalFilename(), file.getSize());

        Image image = imageMapper.mapToImage(file, name, tags); // Como foi retirado a lógica de montar a imagem daqui e colocado no mapper, executo o métdo que retorna a imagem construida
        Image imageSaved = imageService.save(image); // Depois acesso o service para passar a imagem para salvar.
        URI imageURI = buildImageURL(imageSaved); // Ao retornar já tenho o ID da imagem que foi salva

        return ResponseEntity.created(imageURI).build();
    }

    //Objetivo localhost:8080/v1/images/images?extension=PNG&query=Nature criar link com a pesquisa
    @GetMapping
    public ResponseEntity<List<ImageDTO>> search(
            @RequestParam(value = "extension", required = false, defaultValue = "") String extension, // RequestParam aceita o valor a do parâmetro, se pode ser null ou não e um valor padrão caso precise
            @RequestParam(value = "query", required = false) String query) {

            var result = imageService.search(ImageExtension.ofName(extension), query);

            var images = result.stream().map(image -> {
                var url = buildImageURL(image); // gerando url das imagens com o méttodo já criado
                return imageMapper.imageToDTO(image, url.toString()); // retornando a imagem com a url
            }).collect(Collectors.toList()); // transformando em uma lista de imagens
            return ResponseEntity.ok(images); // OK e levando as imagens criadas
    }

    @GetMapping("{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        var possibleImage = imageService.getById(id);

        if (possibleImage.isEmpty()) {
            return ResponseEntity.notFound().build(); // build é diferente do build do Lombok
        }

        var image = possibleImage.get();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(image.getExtension().getMediaType());
        httpHeaders.setContentLength(image.getSize());
        httpHeaders.setContentDispositionFormData("inline; filename=\"" + image.getFileName() + "\"", image.getFileName());
        return new ResponseEntity<>(image.getFile(), httpHeaders, HttpStatus.OK);
    }

    public URI buildImageURL(Image image) { // Aqui é para montar a URL da imagem ao colocar no banco de daoos
        String path = "/" + image.getId(); // Crio o path com o id da imagem
        return ServletUriComponentsBuilder // É retornando a URL atual com o fromCurrentRequest passando o novo path "/+idImagem", que adiciona
                .fromCurrentRequestUri()    // Com isso é montado, toUri para transformar na URI e depois retornar
                .path(path)
                .build().toUri();
    }
}
