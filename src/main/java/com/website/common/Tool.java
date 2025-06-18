package com.website.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class Tool {
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일을 업로드하는 메서드 파일은 C:\project-files-bu200ServerFile 에 저장된다.
     * 경로가 없다면 생성한다. 에러가 나면 메세지 출력 후 null 을 응답한다. 윈도우에서만 사용 가능.
     * 파일을 확인하고 싶다면 --"http://localhost:8080/uploads/변경된파일명"-- 입력 시 확인 가능
     * @param file 파일 하나를 받음
     * @return UUID로 변경된 파일 이름
     */
    public String upload(MultipartFile file) {
        if(file.getOriginalFilename() != null) {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());            //파일 이름
            String changedFileName = UUID.randomUUID() + "_" + originalFileName;                    //변경된 파일 이름은 랜덤 UUID_원본파일명 형식으로 저장된다.
            System.out.println(changedFileName);
            try{
                String filePath = uploadDir + File.separator + changedFileName;
                File dest = new File(filePath);
                File directory = new File(dest.getParent());
                if(!directory.exists()){
                    if(directory.mkdirs()){
                        System.out.println("경로가 존재하지 않아 생성하였습니다.");
                    } else {    //경로생성에 실패한 상황
                        throw new IOException("경로가 존재하지 않아 생성하려고 했으나 실패했습니다. : " + directory.getAbsolutePath());
                    }
                }
                file.transferTo(dest);  //저장
                return changedFileName;
            } catch (IOException e) {   //파일 저장 중 에러 발생 상황
                System.err.println(e.getMessage());
                return null;
            }
        }
        return null;
    }
}
