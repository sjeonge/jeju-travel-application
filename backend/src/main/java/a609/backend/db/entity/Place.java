package a609.backend.db.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PLACE_UID")
    private Long placeUid;

    @Column(name="PLACE_NAME")
    private String placeName;

    @Column(name="PLACE_TYPE")
    private Integer placeType;

    @Column(name="ROAD_ADDRESS")
    private String roadAddress;

    @Column(name="IMG_PATH")
    private String imgPath;

    @Column(name="LAT")
    private Double lat;

    @Column(name="LNG")
    private Double lng;

    @Column(name="THUMBS")
    private Integer Thumbs;

    @Column(name="STYLE")
    private Integer style;

    @Column(name="TAG")
    private String tag;

    @Column(name="TIRE")
    private Integer tire;


}
