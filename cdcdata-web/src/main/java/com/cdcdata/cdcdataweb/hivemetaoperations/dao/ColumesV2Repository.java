package com.cdcdata.cdcdataweb.hivemetaoperations.dao;

import com.cdcdata.cdcdataweb.hivemetaoperations.domain.TBLS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColumesV2Repository extends JpaRepository<TBLS,Long> {

    @Query(value = "select column_name,type_name,integer_idx from columns_v2 c left join sds s on c.cd_id = s.cd_id left join tbls t on s.sd_id = t.sd_id where t.tbl_id = ?1",nativeQuery = true)
    public List<Object[]> findTableTypesByDbId(Long dbId);

}
