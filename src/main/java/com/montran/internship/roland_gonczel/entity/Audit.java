package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.Operation;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MappedSuperclass
public class Audit {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column
    private String initiatorUsername;

    @Column
    private Operation operation;

    @Column
    private boolean waitingResponse;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

}
