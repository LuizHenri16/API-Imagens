package io.github.dougllasfps.imageliteapi.infra.repository.specs;

import io.github.dougllasfps.imageliteapi.domain.entity.Image;
import io.github.dougllasfps.imageliteapi.domain.enums.ImageExtension;
import org.springframework.data.jpa.domain.Specification;

public class GenericSpecs {
    private GenericSpecs() {}

    public static <T> Specification<T> conjunction() {
        return (root, query1, cb) -> cb.conjunction();
    }

}
