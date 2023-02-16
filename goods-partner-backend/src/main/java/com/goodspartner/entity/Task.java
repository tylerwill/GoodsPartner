package com.goodspartner.entity;


import com.goodspartner.dto.MapPoint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_id_sequence")
    @SequenceGenerator(name = "tasks_id_sequence", sequenceName = "tasks_id_sequence")
    @Column(name = "id")
    private long id;

    @Column(name = "description")
    private String description;

    @Column(name = "execution_date")
    private LocalDate executionDate;

    @Type(type = "JSONB")
    @Column(columnDefinition = "jsonb")
    private MapPoint mapPoint;

    @OneToOne
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private Car car;

}
