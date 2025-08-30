package com.SpringBoot.SeatingService.dto;

import lombok.Data;

@Data
public class SeatingChartRequest {
    private String layoutJson;

    public SeatingChartRequest() {} 

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }
}