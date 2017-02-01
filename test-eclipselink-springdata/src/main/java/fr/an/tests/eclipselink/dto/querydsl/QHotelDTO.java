package fr.an.tests.eclipselink.dto.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SetPath;
import com.querydsl.core.types.dsl.StringPath;

import fr.an.tests.eclipselink.dto.HotelDTO;
import fr.an.tests.eclipselink.dto.ReviewDTO;


/**
 * QHotel is a Querydsl query type for Hotel
 */
public class QHotelDTO extends EntityPathBase<HotelDTO> {

    private static final long serialVersionUID = -1929614749L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHotelDTO hotel = new QHotelDTO("hotel");

    public final StringPath address = createString("address");

    public final QCityDTO city;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final SetPath<ReviewDTO, QReviewDTO> reviews = this.<ReviewDTO, QReviewDTO>createSet("reviews", ReviewDTO.class, QReviewDTO.class, PathInits.DIRECT2);

    public final StringPath zip = createString("zip");

    public QHotelDTO(String variable) {
        this(HotelDTO.class, forVariable(variable), INITS);
    }

    public QHotelDTO(Path<? extends HotelDTO> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHotelDTO(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHotelDTO(PathMetadata metadata, PathInits inits) {
        this(HotelDTO.class, metadata, inits);
    }

    public QHotelDTO(Class<? extends HotelDTO> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.city = inits.isInitialized("city") ? new QCityDTO(forProperty("city")) : null;
    }

}

