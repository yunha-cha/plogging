package com.website.websocket.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter@ToString
public class AlertDTO {
    private String alertId;
    private String alertTitle;
    private String alertDetail;
    private String alertDate;

    private Integer alertSender;
    private String deptName;
}
