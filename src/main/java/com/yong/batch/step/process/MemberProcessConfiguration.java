package com.yong.batch.step.process;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberProcessConfiguration {

    @SuppressWarnings("rawtypes")
    @Bean("memberItemProcess")
    public ItemProcessor memberItemProcess() {
        return new MemberItemProcessor();
    }
}

