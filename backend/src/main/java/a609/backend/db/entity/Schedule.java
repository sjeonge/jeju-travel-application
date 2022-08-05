package a609.backend.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Schedule {

    //turn이 0인 스케쥴을 만들어서 소요시간을 시작시간으로 설정해준다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="SCHEDULE_ID")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLACE_ID")
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRIP_ID")
    private Trip trip;

    @Column(name="STAY_TIME")
    private Integer stayTime;

    @Column(name="COST")
    private Integer cost;

    @Column(name="IS_FIXED")
    private Boolean isFixed;

    @Column(name="DAY")
    private Integer day;

    @Column(name="TURN")
    private Integer turn;

}