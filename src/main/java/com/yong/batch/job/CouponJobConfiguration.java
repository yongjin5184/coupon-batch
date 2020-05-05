package com.yong.batch.job;


import com.yong.batch.model.DTO.MemberDTO;
import com.yong.batch.param.CurrentTimeIncrementer;
import com.yong.batch.step.reader.RedisItemReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = "couponJob")
public class CouponJobConfiguration {

    private static final String JOB_NAME = "couponJob";
    private static final String STEP_NAME = "couponStep";
    private static final String STATEMENT_ID = "insertCoupon";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionMasterFactory;
    private final ItemProcessor memberItemProcess;

    public CouponJobConfiguration(JobBuilderFactory jobBuilderFactory,
         StepBuilderFactory stepBuilderFactory,
        @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionMasterFactory,
        @Qualifier("memberItemProcess") ItemProcessor memberItemProcess) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.sqlSessionMasterFactory = sqlSessionMasterFactory;
        this.memberItemProcess = memberItemProcess;
    }

    @Bean(name="couponJob")
    public Job couponJob() {
        return jobBuilderFactory.get(JOB_NAME)
            .incrementer(new CurrentTimeIncrementer())
            .flow(couponStep())
            .end()
            .build();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public Step couponStep() {
        return stepBuilderFactory.get(STEP_NAME)
            .chunk(1)
            .reader(reader2())
            .processor(memberItemProcess)
            .writer(writer2())
            .build();
    }

    @Bean
    public RedisItemReader reader2() {
        return new RedisItemReader();
    }

    @Bean
    public MyBatisBatchItemWriter<MemberDTO> writer2(){
        return new MyBatisBatchItemWriterBuilder<MemberDTO>()
            .sqlSessionFactory(sqlSessionMasterFactory)
            .statementId(STATEMENT_ID)
            .build();
    }
}
