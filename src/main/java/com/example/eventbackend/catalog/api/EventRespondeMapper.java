public class EventResponseMapper {
    public static EventResponse fromProjection(EventProjection projection) {
        return EventResponse.builder()
                .id(projection.getId())
                .title(projection.getTitle())
                .cover(projection.getCover())
                .likedCount(projection.getLikedCount())
                .isSoldOut(projection.isSoldOut())
                .startAt(projection.getStartAt())
                .lowestPrice(projection.getLowestPrice())
                .description(projection.getDescription())
                .venue(EventResponse.VenueResponse.builder()
                        .name(projection.getVenueName())
                        .adresse(projection.getVenueAdresse())
                        .coordinates(
                                EventResponse.VenueResponse.CoordinatesResponse.builder()
                                        .latitude(projection.getVenueLat())
                                        .longitude(projection.getVenueLng())
                                        .build()
                        )
                        .build())
                .build();
    }
}
