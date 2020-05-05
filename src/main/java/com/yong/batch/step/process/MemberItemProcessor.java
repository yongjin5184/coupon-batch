package com.yong.batch.step.process;


import com.yong.batch.model.DTO.MemberDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MemberItemProcessor implements ItemProcessor<MemberDTO, MemberDTO>{

    @Override
    public MemberDTO process(MemberDTO memberDTO) {
        System.out.println("여기4 " +  memberDTO.toString());
        log.info("### memberDto -> memberDto : item = {}", memberDTO.toString());
        return memberDTO;
    }
}

