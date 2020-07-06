package com.cdcdata.cdcdataweb.hivemetaoperations.controller;

import com.cdcdata.cdcdataweb.hivemetaoperations.domain.Columns;
import com.cdcdata.cdcdataweb.hivemetaoperations.domain.DBS;
import com.cdcdata.cdcdataweb.hivemetaoperations.domain.TBLS;
import com.cdcdata.cdcdataweb.hivemetaoperations.dao.ColumesV2Repository;
import com.cdcdata.cdcdataweb.hivemetaoperations.dao.DBSRepository;
import com.cdcdata.cdcdataweb.hivemetaoperations.dao.TBLSRepository;
import com.cdcdata.cdcdataweb.hivemetaoperations.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HiveMetaController {

    private final static Logger log = LoggerFactory.getLogger(HiveMetaController.class);

    @Autowired
    private DBSRepository dbsMetaRepository;

    /**
     * 查询出DBS的信息
     * @return
     */
    @GetMapping("/selectAllDBS")
    public List<DBS> selectAllDBS(){
        List<DBS> list = null;
        try {
           list = dbsMetaRepository.findAll();
        } catch (Exception e){
            log.error("selectAllDBS error");
        }

        return list;

    }

    /**
     * 向DBS表增加数据
     * 因为数据库字段是非自增 所以自己生成id 自定义sql语句插入数据
     * @param dbs
     * @return
     */
    @PostMapping("/insertDBS")
    public Boolean insertDBS(DBS dbs){
        Boolean flag = false;
        try {
            IdWorker idWorker=new IdWorker(0,0); //机器id 与序列id ，也可以不传
            long id = idWorker.nextId();
            dbsMetaRepository.insertDBS(id,dbs.getDbLocationUri(),dbs.getName());
            flag = true;
        } catch (Exception e){
            flag = false;
            log.error("insertDBS error");
        }
        return flag;

    }

    /**
     * 根据id删除DBS的某一条数据
     * 因为dbs的主键时tbls的外键 所以删除dbs的数据前先把tbls表中对应的外键删除 然后再删除dbs中的数据
     * @param id
     * @return
     */
    @DeleteMapping("/deleteDBS")
    public Object insertDBS(String id){
        Boolean flag = false;
        try {
            List<TBLS> list = tblsRepository.findTablesByDbId(Long.parseLong(id));
            for(int i = 0;i<list.size();i++){
                TBLS tbls = list.get(i);
                tbls.setDbId(null);
            }
            tblsRepository.saveAll(list);
            dbsMetaRepository.deleteById(Long.parseLong(id));
            flag = true;
        }catch (Exception e){
            flag = false;
            log.error("deleteDBS error");
            e.printStackTrace();
        }
        return flag;

    }

    @Autowired
    private TBLSRepository tblsRepository;

    /**
     * 根据数据库id查询出表id name type字段
     * @param id
     * @return
     */
    @GetMapping("/selectTBS")
    public List<TBLS> selectTBS(String id){
        List<TBLS> list = null;
        try {
            list = tblsRepository.findTablesByDbId(Long.parseLong(id));
        }catch (Exception e){
            log.error("selectTBS error");
        }
        return list;

    }

    @Autowired
    private ColumesV2Repository columesV2Repository;

    /**
     * 根据表id查询出字段名 字段类型喝字段index
     * 三表关联 自定义查询语句
     *
     首先从表TBLS中根据表id(TBL_ID),查询出对应的SD_ID，然后从表 SDS 中根据SD_ID查询出对应的CD_ID
     然后根据CD_ID，从表COLUMNS_V2中查询对应的字段名
     * @param id
     * @return
     */
    @GetMapping("/selectTBTypes")
    public List<Columns> selectTBTypes(String id){

        List<Columns> list = new ArrayList<>();
        try {
            List<Object[]> listObj = columesV2Repository.findTableTypesByDbId(Long.parseLong(id));
            for (int i=0;i < listObj.size();i++){

                Object[] objects = listObj.get(i);
                String columeName = objects[0].toString();
                String typeName = objects[1].toString();
                int integerIdx = Integer.parseInt(objects[2].toString());
                Columns columns = new Columns(columeName,typeName,integerIdx);
                list.add(columns);
            }
        } catch (Exception e){
            log.error("selectTBTypes error");
        }
        return list;

    }
}
