package com.website.websocket.controller;

import com.website.websocket.dto.AlertDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class AlertController {
    private final SimpMessagingTemplate simp;
    public AlertController(SimpMessagingTemplate simp) {
        this.simp = simp;
    }

    /**
     * 클라이언트에서 /send/alert/forum 경로로 보냈을 때
     * @param alertDTO 알람 DTO
     */
    @MessageMapping(value = "/alert/forum")
    public void sendAlert(AlertDTO alertDTO){
        //DTO의 부서 이름으로 부서 코드 조회
        System.out.println("메세지 매핑");
        int deptCode = 1;
        alertDTO.setAlertTitle("부서 or 팀 이름"+" 전체 공지 사항");
        simp.convertAndSend("/sub/alert/forum"+deptCode);
    }
    @MessageMapping("/test")
    public void sendTest(){
        System.out.println("매핑 성공");
        simp.convertAndSend("/sub","이거도 됨?");
        simp.convertAndSend("/sub/alert/forum","하이");

    }
}
