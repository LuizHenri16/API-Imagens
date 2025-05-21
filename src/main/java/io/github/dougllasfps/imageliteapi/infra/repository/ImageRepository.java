package io.github.dougllasfps.imageliteapi.infra.repository;


import io.github.dougllasfps.imageliteapi.domain.entity.Image;
import io.github.dougllasfps.imageliteapi.domain.enums.ImageExtension;
import io.github.dougllasfps.imageliteapi.infra.repository.specs.GenericSpecs;
import io.github.dougllasfps.imageliteapi.infra.repository.specs.ImageSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.github.dougllasfps.imageliteapi.infra.repository.specs.ImageSpecs.extensionEqual;

@Repository
public interface ImageRepository extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {

    default List<Image> findByExtensionAndNameOrTagasLike(ImageExtension extension, String query) {

        Specification<Image> specification = Specification.where(GenericSpecs.conjunction());

        if (extension != null) {
            specification = specification.and(extensionEqual(extension));
            //  Criando a verificão para a extensão, onde será pego somente imagens que contenham a extensão
        }

        if (StringUtils.hasText(query)) {
            Specification<Image> nameOrTagsSpecification = Specification.anyOf(ImageSpecs.nameLike(query), ImageSpecs.tagLike(query));
            specification = specification.and(nameOrTagsSpecification);
        }
        return findAll(specification);
    }
}
