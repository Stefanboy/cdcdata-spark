package com.cdcdata.cdcdataweb.hivemetaoperations.dao;

import com.cdcdata.cdcdataweb.hivemetaoperations.domain.DBS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface DBSRepository extends JpaRepository<DBS,Long> {
    @Transactional
    @Modifying
    @Query(value = "insert into dbs(db_id,db_location_uri,name) values(?1,?2,?3)",nativeQuery = true)
    public void insertDBS(Long id,String dbLocationUri,String name);

}
