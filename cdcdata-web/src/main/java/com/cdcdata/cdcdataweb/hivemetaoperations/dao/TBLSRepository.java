package com.cdcdata.cdcdataweb.hivemetaoperations.dao;

import com.cdcdata.cdcdataweb.hivemetaoperations.domain.TBLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TBLSRepository extends JpaRepository<TBLS,Long> {

    @Query(value = "select * from TBLS where DB_ID=?1",nativeQuery = true)
    public List<TBLS> findTablesByDbId(Long dbId);

}
