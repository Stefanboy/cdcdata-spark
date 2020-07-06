package com.cdcdata.spark.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FileUtils {


    public static void delete(Configuration conf,String out) throws Exception{
        FileSystem fileSystem = FileSystem.get(conf);
        Path outputPath = new Path(out);
        if(fileSystem.exists(outputPath)){
            fileSystem.delete(outputPath,true);

        }

    }


}
