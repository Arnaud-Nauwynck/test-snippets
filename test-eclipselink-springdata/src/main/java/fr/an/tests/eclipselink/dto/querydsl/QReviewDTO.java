package fr.an.tests.eclipselink.dto.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;

import fr.an.tests.eclipselink.dto.RatingDTO;
import fr.an.tests.eclipselink.dto.ReviewDTO;
import fr.an.tests.eclipselink.dto.TripType;


/**
 * QReview is a Querydsl query type for Review
 */
public class QReviewDTO extends EntityPathBase<ReviewDTO> {

    private static final long serialVersionUID = 588604553L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewDTO review = new QReviewDTO("review");

    public final DatePath<java.util.Date> checkInDate = createDate("checkInDate", java.util.Date.class);

    public final StringPath details = createString("details");

    public final QHotelDTO hotel;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> index = createNumber("index", Integer.class);

    public final EnumPath<RatingDTO> rating = createEnum("rating", RatingDTO.class);

    public final StringPath title = createString("title");

    public final EnumPath<TripType> tripType = createEnum("tripType", TripType.class);

    public QReviewDTO(String variable) {
        this(ReviewDTO.class, forVariable(variable), INITS);
    }

    public QReviewDTO(Path<? extends ReviewDTO> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewDTO(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewDTO(PathMetadata metadata, PathInits inits) {
        this(ReviewDTO.class, metadata, inits);
    }

    public QReviewDTO(Class<? extends ReviewDTO> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hotel = inits.isInitialized("hotel") ? new QHotelDTO(forProperty("hotel"), inits.get("hotel")) : null;
    }

}

