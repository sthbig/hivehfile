<h1>README</h1>

<h3>名词解释</h3>
<ul>
<li>mappingInfo		： Hive字段和Hbase列的映射组关系。每一个映射组由一个或多个column-mapping构成，其表达了一套Hive字段与HBase字段的对应关系。</li>
<li>column-mapping  :  单个Hive字段与HBase字段的对应关系。</li>
</ul>
<hr>
<h3>配置文件组成部分</h3><br>
<ol>
<li>全局设置
    <ul>
	<li> 输入的数据的HDFS路径<input-path> 	        				[常修改]</li>
	<li> 输出HFile的HDFS路径 <output-path>							[常修改]</li>
	<li> HBase表名 <htable-name>										[常修改]</li>
	<li> Hive表的字段分隔符 <field-delimiter>						[不常修改]</li>
	<li> Hive表Array元素的分隔符 <collection-item-delimiter>			[不常修改]</li>
	<li> HBase集群配置（用于读取HBase表的Region信息）				[不修改]
	    <ul>
		    <li> &lt;hbase.zookeeper.quorum&gt;
		    <li> &lt;hbase.zookeeper.property.clientPort&gt;
		    <li> &lt;hbase.zookeeper.property.maxClientCnxns&gt;
		    <li> &lt;hbase.znode.parent&gt;
		</ul>
	</li>
	</ul>	
</li>		
<li>局部设置
	<ul>
	    <li>Hive表字段与HBase列的映射关系 &lt;MappingInfo&gt;，关于 &lt;MappingInfo&gt;的具体填写方式见后文   [常修改]</li>
	</ul>
</li>
</ol>
<hr>
<h3>MappingInfo结构<h3>
<p>|- MappingInfo</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- partition</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- column-mapping</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- hive-column-name</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- hive-column-type</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- rowkey</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- hbase-column-family</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|- hbase-column-qualifier</p>
<hr>
<h3>MappingInfo参考样例</h3>
<pre>
&lt;mapping-info&gt;
	&lt;partition&gt;
		feature=black_wifi_cnt/data_date=20170420,
		feature=black_wifi_cnt/data_date=20170421,
		feature=black_wifi_cnt/data_date=20170425
	&lt;/partition&gt;
    &lt;column-mapping&gt;
        &lt;hive-column-name&gt;jid&lt;/hive-column-name&gt;
        &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
    &lt;/column-mapping&gt;
    &lt;column-mapping&gt;
        &lt;hive-column-name&gt;imei&lt;/hive-column-name&gt;
        &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
        &lt;rowkey&gt;true&lt;/rowkey&gt;
    &lt;/column-mapping&gt;
    &lt;column-mapping&gt;
        &lt;hive-column-name&gt;value&lt;/hive-column-name&gt;
        &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
        &lt;hbase-column-family&gt;A&lt;/hbase-column-family&gt;
        &lt;hbase-column-qualifier&gt;relationship_risk&lt;/hbase-column-qualifier&gt;
    &lt;/column-mapping&gt;
&lt;/mapping-info&gt;
</pre>
<p><strong>描述</strong></p>
<ul>
    <li>若表是分区表，则填写&lt;partition&gt;标签。在上面例子中，feature和data_date均为分区字段;</li>
    <li>&lt;column-mapping&gt;表示一个Hive表字段和一个HBase字段的对应关系;</li>
    <li>填写&lt;column-mapping&gt;时，【必须】按照Hive表字段的顺序填写，这关系到Hive数据文件的解析结果。如果未按照Hive字段顺序填写，有可能出现字段和值错位的情况;</li>
    <li>如果一个Hive字段需要作为HBase的rowkey,则&lt;rowkey&gt;标记为true，如上例的imei字段；</li>
    <li>如果一个字段不需要写入HBase，则只填写该字段的&lt;hive-column-name&gt;和&lt;hive-column-type&gt;，如上例子的jid字段；</li>
    <li>如果一个字段需要写入HBase，则需要填写HBase的ColumnFamily和ColumnQualifier信息，即填写&lt;hbase-column-family&gt;和&lt;hbase-column-qualifier&gt;标签，如上例的value字段;</li>

    <li>对于非分区表，则没有&lt;partition&gt;标签，且一个配置文件只允许含有一个MappingInfo。（当非分区表存在多个MappingInfo时，无法判断一条Hive记录需要通过哪一个MappingInfo进行处理）</li>
</ul>
<hr>
<h3>配置文件的参考样例</h3>
<strong>不含占位符的配置文件</strong>
<pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;config&gt;
	&lt;!-- Global Settings --&gt;
	&lt;input-path&gt;hdfs://nameservice1/tmp/test-hfile-input&lt;/input-path&gt;
    &lt;output-path&gt;hdfs://nameservice1/tmp/test-hfile-output&lt;/output-path&gt;
    &lt;htable-name&gt;fraud_feature_nor&lt;/htable-name&gt;
    &lt;field-delimiter&gt;\u0001&lt;/field-delimiter&gt;
    &lt;collection-item-delimiter&gt;,&lt;/collection-item-delimiter&gt;
    &lt;hbase.zookeeper.quorum&gt;192.168.254.71,192.168.254.72,192.168.254.73&lt;/hbase.zookeeper.quorum&gt;
    &lt;hbase.zookeeper.property.clientPort&gt;2181&lt;/hbase.zookeeper.property.clientPort&gt;
    &lt;hbase.zookeeper.property.maxClientCnxns&gt;400&lt;/hbase.zookeeper.property.maxClientCnxns&gt;
    &lt;hbase.znode.parent&gt;/hbase&lt;/hbase.znode.parent&gt;

	&lt;!-- Local Settings --&gt;
    &lt;mapping-info&gt;
		&lt;partition&gt;
			feature=black_wifi_cnt/data_date=20170420,
			feature=black_wifi_cnt/data_date=20170421,
			feature=black_wifi_cnt/data_date=20170425
		&lt;/partition&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;imei&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;rowkey&gt;true&lt;/rowkey&gt;
        &lt;/column-mapping&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;value&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;hbase-column-family&gt;A&lt;/hbase-column-family&gt;
            &lt;hbase-column-qualifier&gt;relationship_risk&lt;/hbase-column-qualifier&gt;
        &lt;/column-mapping&gt;
    &lt;/mapping-info&gt;

	&lt;mapping-info&gt;
		&lt;partition&gt;
			feature=install_pkg_cnt/data_date=20170423,
			feature=install_pkg_cnt/data_date=20170424,
			feature=install_pkg_cnt/data_date=20170425
		&lt;/partition&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;imei&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;rowkey&gt;true&lt;/rowkey&gt;
        &lt;/column-mapping&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;value&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;hbase-column-family&gt;A&lt;/hbase-column-family&gt;
            &lt;hbase-column-qualifier&gt;total_app_cnt&lt;/hbase-column-qualifier&gt;
        &lt;/column-mapping&gt;
    &lt;/mapping-info&gt;
&lt;/config&gt;
</pre>
<strong>含占位符的配置文件</strong>
<pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;config&gt;
	&lt;!-- Global Settings --&gt;
	&lt;input-path&gt;${input-path}&lt;input-path&gt;
    &lt;output-path&gt;${output-path}&lt;/output-path&gt;
    &lt;htable-name&gt;fraud_feature_nor&lt;/htable-name&gt;
    &lt;field-delimiter&gt;\u0001&lt;/field-delimiter&gt;
    &lt;collection-item-delimiter&gt;,&lt;/collection-item-delimiter&gt;
    &lt;hbase.zookeeper.quorum&gt;192.168.254.71,192.168.254.72,192.168.254.73&lt;/hbase.zookeeper.quorum&gt;
    &lt;hbase.zookeeper.property.clientPort&gt;2181&lt;/hbase.zookeeper.property.clientPort&gt;
    &lt;hbase.zookeeper.property.maxClientCnxns&gt;400&lt;/hbase.zookeeper.property.maxClientCnxns&gt;
    &lt;hbase.znode.parent&gt;/hbase&lt;/hbase.znode.parent&gt;

	&lt;!-- Local Settings --&gt;
    &lt;mapping-info&gt;
		&lt;partition&gt;
			${partition}
		&lt;/partition&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;imei&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;rowkey&gt;true&lt;/rowkey&gt;
        &lt;/column-mapping&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;value&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;hbase-column-family&gt;A&lt;/hbase-column-family&gt;
            &lt;hbase-column-qualifier&gt;relationship_risk&lt;/hbase-column-qualifier&gt;
        &lt;/column-mapping&gt;
    &lt;/mapping-info&gt;

	&lt;mapping-info&gt;
		&lt;partition&gt;
			feature=install_pkg_cnt/data_date=20170423,
			feature=install_pkg_cnt/data_date=20170424,
			feature=install_pkg_cnt/data_date=20170425
		&lt;/partition&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;imei&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;rowkey&gt;true&lt;/rowkey&gt;
        &lt;/column-mapping&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;value&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;hbase-column-family&gt;A&lt;/hbase-column-family&gt;
            &lt;hbase-column-qualifier&gt;total_app_cnt&lt;/hbase-column-qualifier&gt;
        &lt;/column-mapping&gt;
    &lt;/mapping-info&gt;
&lt;/config&gt;
</pre>
<strong>HBase 列族和列名自动填充的配置文件(支持填写表字段，包含分区字段)</strong>
<pre>
&lt;?xml version="1.0" encoding="UTF-8" ?&gt;
&lt;config&gt;
	&lt;!-- Global Settings --&gt;
	&lt;input-path&gt;hdfs://nameservice1/tmp/test-hfile-inpu&lt;input-path&gt;
    &lt;output-path&gt;hdfs://nameservice1/tmp/test-hfile-output&lt;/output-path&gt;
    &lt;htable-name&gt;fraud_feature_nor&lt;/htable-name&gt;
    &lt;field-delimiter&gt;\u0001&lt;/field-delimiter&gt;
    &lt;collection-item-delimiter&gt;,&lt;/collection-item-delimiter&gt;
    &lt;hbase.zookeeper.quorum&gt;192.168.254.71,192.168.254.72,192.168.254.73&lt;/hbase.zookeeper.quorum&gt;
    &lt;hbase.zookeeper.property.clientPort&gt;2181&lt;/hbase.zookeeper.property.clientPort&gt;
    &lt;hbase.zookeeper.property.maxClientCnxns&gt;400&lt;/hbase.zookeeper.property.maxClientCnxns&gt;
    &lt;hbase.znode.parent&gt;/hbase&lt;/hbase.znode.parent&gt;

	&lt;!-- Local Settings --&gt;
	&lt;mapping-info&gt;
		&lt;partition&gt;
			data_date=20170423,
			data_date=20170424,
			data_date=20170425
		&lt;/partition&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;imei&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;rowkey&gt;true&lt;/rowkey&gt;
        &lt;/column-mapping&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;value&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
            &lt;hbase-column-family&gt;A&lt;/hbase-column-family&gt;
            &lt;hbase-column-qualifier&gt;<b>#feature#</b>&lt;/hbase-column-qualifier&gt;
        &lt;/column-mapping&gt;
        &lt;column-mapping&gt;
            &lt;hive-column-name&gt;feature&lt;/hive-column-name&gt;
            &lt;hive-column-type&gt;string&lt;/hive-column-type&gt;
        &lt;/column-mapping&gt;        
    &lt;/mapping-info&gt;
&lt;/config&gt;
</pre>
<h4>备注</h4>
<h5>配置文件未涉及HBase字段时间戳的填写</h5>
<p>
默认情况下，如果配置文件的输入路径中含有data_date=yyyyMMdd，则会使用该日期作为数据日期；
如果配置文件的输入路径中不含有日期分区，则会采用当前日期作为数据日期
</p>
<h5>HBase列族名或列名自动填充的功能</h5>
<p>将&lt;hbase-column-family&gt;或&lt;hbase-column-qualifier&gt;标签内容替换为#&lt;填充的字段名&gt;#</p>
<p>e.g.</p>
<p>
需要使用Hive的feature字段值作为HBase的列族名的填写方式：&lt;hbase-column-qualifier&gt;#feature#&lt;/hbase-column-qualifier&gt;
</p>
<h5>当在XML中使用以下5个符号 &lt;、&gt;、&amp;、&apos;、&quot; 时，需要进行转义。转义规则如下：</h5>
<table>
    <tr>
        <td>特殊字符</td>
        <td>转义规则</td>
    </tr>
    <tr>
    	<td>&lt;</td>
    	<td>&amp;lt;</td>
    </tr>
    <tr>
    	<td>&gt;</td>
    	<td>&amp;gt;</td>
    </tr>
    <tr>
    	<td>&amp;</td>
    	<td>&amp;amp;</td>
    </tr>
    <tr>
    	<td>&apos;</td>
    	<td>&amp;apos;</td>
    </tr>
    <tr>
    	<td>&quot;</td>
    	<td>&amp;quot;</td>
    </tr>
</table>
<hr>
<h3>命令行调用方式</h3>
<p>命令行允许携带参数</p>
<p>-D mapreduce.job.queuename 是<b>必填参数</b>，用于指定任务提交的资源池</p>
<p>-config 是<b>必填参数</b>，用于指定配置文件的HDFS路径</p>
<p>-dict 是<b>选填参数</b>，用于传递一个变量字典，以替换配置文件中对应的占位符</p>
<p>-format 是<b>选填参数</b>，用于指定数据文件的格式。如果数据文件的格式为txt，则不需要填写该参数；如果数据文件的格式为parquet，则填写-format parquet</p>
<p>-unique 是<b>选填参数</b>，用于指定是否生成唯一的键值对时间戳。如果需要为键值对生成唯一的时间戳，则在命令行中填写 -unique true</p>
<p>-name 是<b>选填参数</b>用于指定作业的名称。默认情况下，作业名统一为hivehfile.jar。如果需要指定作业名，则在命令行添加参数 -name <jobname></p>
<pre>
yarn jar &lt;jar.name&gt; JobExecutor -D mapreduce.job.queuename=&lt;queuename&gt; -config &lt;config-file.hdfs.path&gt; [-dict &lt;parameter.dict&gt;] [-format &lt;data.format&gt;] [-unique &lt;true or false&gt;] [-name &lt;job.name&gt;]

e.g.
#sample 1
yarn jar hivehfile.jar JobExecutor -D mapreduce.job.queuename=root.etl -config hdfs://nameservice1/tmp/hfile-config/hfile_rt_career-with-params.xml 

#sample 2
yarn jar hivehfile.jar JobExecutor
-D mapreduce.job.queuename=root.etl
-config hdfs://nameservice1/tmp/hfile-config/hfile_rt_career-with-params.xml
-dict "{'input-path':'hdfs://nameservice1/user/hive/warehouse/tmp.db/hfile_rt_career'}"

#sample 3
yarn jar hivehfile.jar JobExecutor  
-D mapreduce.job.queuename=root.etl
-config hdfs://nameservice1/tmp/hfile-config/hfile_rt_career-with-params.xml 
-dict "{'input-path':'hdfs://nameservice1/user/hive/warehouse/tmp.db/hfile_rt_career'}"
-format parquet

#sample 4
yarn jar hivehfile.jar JobExecutor  
-D mapreduce.job.queuename=root.etl
-config hdfs://nameservice1/tmp/hfile-config/hfile_rt_career-with-params.xml 
-dict "{'input-path':'hdfs://nameservice1/user/hive/warehouse/tmp.db/hfile_rt_career'}"
-format parquet
-unique true

#sample 5
yarn jar hivehfile.jar JobExecutor -D mapreduce.job.queuename=root.etl -config /tmp/hfile-config/hfile_rt_career.xml -name hfile-fosun
</pre>






