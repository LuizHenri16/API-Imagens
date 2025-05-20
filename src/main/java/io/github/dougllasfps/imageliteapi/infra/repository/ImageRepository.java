package io.github.dougllasfps.imageliteapi.infra.repository;


import io.github.dougllasfps.imageliteapi.domain.entity.Image;
import io.github.dougllasfps.imageliteapi.domain.enums.ImageExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {

    default List<Image> findByExtensionAndNameOrTagasLike(ImageExtension extension, String query) {

        Specification<Image> conjunction = (root, query1, cb) -> cb.conjunction();
        Specification<Image> specification = Specification.where(conjunction);

        if (extension != null) {
            Specification<Image> extensionEqual = (root, q, cb) -> cb.equal(root.get("extension"), extension);
            specification = specification.and(extensionEqual);
            //  Criando a verificão para a extensão, onde será pego somente imagens que contenham a extensão
        }

        if (StringUtils.hasText(query)) {
            Specification nameLike = (root, query1, cb) -> cb.like(cb.upper(root.get("name")), "%" + query.toUpperCase() + "%");
            Specification tagsLike = (root, query1, cb) -> cb.like(cb.upper(root.get("tags")), "%" + query.toUpperCase() + "%");

            Specification nameOrTagsSpecification = Specification.anyOf(nameLike, tagsLike);
            specification.and(nameOrTagsSpecification);
        }

        return findAll(specification);
    }
}
