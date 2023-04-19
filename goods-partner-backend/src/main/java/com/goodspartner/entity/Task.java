package com.goodspartner.entity;


import com.goodspartner.dto.MapPoint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    public void setAttachments(List<Attachment> attachments) {
        this.attachments.clear();
        this.addAttachments(attachments);
    }

    public void addAttachments(List<Attachment> attachments) {
        List<Attachment> requiredAttachments = Optional.ofNullable(attachments)
                .orElse(Collections.emptyList());
        this.attachments.addAll(requiredAttachments);
        this.attachments.forEach(attachment -> attachment.setTask(this));
    }
}
