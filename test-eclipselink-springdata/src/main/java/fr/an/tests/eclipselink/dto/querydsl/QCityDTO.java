package fr.an.tests.eclipselink.dto.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import fr.an.tests.eclipselink.dto.CityDTO;


/**
 * QCity is a Querydsl query type for City
 */
public class QCityDTO extends EntityPathBase<CityDTO> {

    private static final long serialVersionUID = 1600167644L;

    public static final QCityDTO city = new QCityDTO("city");

    public final StringPath country = createString("country");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath map = createString("map");

    public final StringPath name = createString("name");

    public final StringPath state = createString("state");

    public QCityDTO(String variable) {
        super(CityDTO.class, forVariable(variable));
    }

    public QCityDTO(Path<? extends CityDTO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCityDTO(PathMetadata metadata) {
        super(CityDTO.class, metadata);
    }

}

