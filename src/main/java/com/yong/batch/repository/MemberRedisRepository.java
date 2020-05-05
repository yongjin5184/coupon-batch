package com.yong.batch.repository;

import com.yong.batch.model.DTO.MemberDTO;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRedisRepository extends PagingAndSortingRepository<MemberDTO, Long> {
}
