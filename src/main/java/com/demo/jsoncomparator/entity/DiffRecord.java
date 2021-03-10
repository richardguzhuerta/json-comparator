package com.demo.jsoncomparator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Builder
@Entity
@Table(name = "DIFF_RECORD")
@NoArgsConstructor
@AllArgsConstructor
public class DiffRecord implements Serializable {
    @Id
    private String requestId;
    private String leftFile;
    private String rightFile;
    private String comparisonResult;

}
