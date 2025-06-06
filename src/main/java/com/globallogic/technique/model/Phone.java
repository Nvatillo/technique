package com.globallogic.technique.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;


@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Phone {
    private long number;
    private int citycode;
    private String contrycode;

}