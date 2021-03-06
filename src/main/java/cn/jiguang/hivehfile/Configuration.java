package cn.jiguang.hivehfile;

import cn.jiguang.hivehfile.model.MappingInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fitz on 2017/5/7.
 * Description:
 * 存储配置文件解析内容，方便作业调用
 */
public class Configuration {
    private String
            htableName,
            inputPath,
            outputPath,
            hbaseZookeeperQuorum,
            hbaseZookeeperPropertyClientPort,
            hbaseZookeeperPropertyMaxClientCnxns,
            hbaseZnodeParent;

    private ArrayList<MappingInfo> mappingInfoList;

    private HashMap<String,String> delimiterCollection;

    /**
     * 根据 InputPath+Partition ,生成所有待读取的文件路径
     * @return
     */
    public String getAllInputPath(){
        // 当 inputPath 以／结尾时，删除结尾的/
        if(inputPath.endsWith("/")){
            inputPath = inputPath.substring(0,inputPath.length()-1);
        }
        StringBuffer sb = new StringBuffer();
        for(MappingInfo $m : mappingInfoList){
            if($m.getPartition() != null && !"".equals($m.getPartition())){
                String[] subPath = $m.getPartition().replaceAll("\\s*","").split(",");
                for(String $s : subPath){
                    sb.append(inputPath+"/"+$s+",");
                }
            }else{
                sb.append(inputPath+",");
            }
        }
        return sb.toString().substring(0,sb.length()-1);
    }

    public ArrayList<MappingInfo> getMappingInfoList() {
        return mappingInfoList;
    }

    public void setMappingInfoList(ArrayList<MappingInfo> mappingInfoList) {
        this.mappingInfoList = mappingInfoList;
    }

    public String getHbaseZookeeperQuorum() {
        return hbaseZookeeperQuorum;
    }

    public void setHbaseZookeeperQuorum(String hbaseZookeeperQuorum) {
        this.hbaseZookeeperQuorum = hbaseZookeeperQuorum;
    }

    public String getHbaseZookeeperPropertyClientPort() {
        return hbaseZookeeperPropertyClientPort;
    }

    public void setHbaseZookeeperPropertyClientPort(String hbaseZookeeperPropertyClientPort) {
        this.hbaseZookeeperPropertyClientPort = hbaseZookeeperPropertyClientPort;
    }

    public String getHbaseZookeeperPropertyMaxClientCnxns() {
        return hbaseZookeeperPropertyMaxClientCnxns;
    }

    public void setHbaseZookeeperPropertyMaxClientCnxns(String hbaseZookeeperPropertyMaxClientCnxns) {
        this.hbaseZookeeperPropertyMaxClientCnxns = hbaseZookeeperPropertyMaxClientCnxns;
    }

    public String getHbaseZnodeParent() {
        return hbaseZnodeParent;
    }

    public void setHbaseZnodeParent(String hbaseZnodeParent) {
        this.hbaseZnodeParent = hbaseZnodeParent;
    }

    public HashMap<String, String> getDelimiterCollection() {
        return delimiterCollection;
    }

    public void setDelimiterCollection(HashMap<String, String> delimiterCollection) {
        this.delimiterCollection = delimiterCollection;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getHtableName() {
        return htableName;
    }

    public void setHtableName(String htableName) {
        this.htableName = htableName;
    }
}

