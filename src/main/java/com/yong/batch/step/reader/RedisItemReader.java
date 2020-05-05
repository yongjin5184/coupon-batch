package com.yong.batch.step.reader;

import com.yong.batch.model.DTO.MemberDTO;
import com.yong.batch.repository.MemberRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Slf4j
public class RedisItemReader implements ItemReader<MemberDTO> {

    @Autowired
    private MemberRedisRepository memberRedisRepository;

    @Override
    public MemberDTO read() {
        Page<MemberDTO> memberDTOS = memberRedisRepository.findAll(PageRequest.of(0, 10));
        log.info("memberDTO2 = {}" , memberDTOS.get().findFirst().get().toString());
        return memberDTOS.get().findFirst().get();
    }
}
