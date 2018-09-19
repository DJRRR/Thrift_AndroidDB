package cn.fudan.libdb.crawler.crawlerEntity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author qiaoying
 * @date 2018/9/17 14:22
 */
@DatabaseTable(tableName = "task_info")
public class CrawlerTaskInfo {
    @DatabaseField(generatedId = true) public int taskId;
    @DatabaseField(canBeNull = false) public String market;
    @DatabaseField(canBeNull=false) public Date createDate;
    @DatabaseField(canBeNull=false) public String taskCreator;
    @DatabaseField(canBeNull=false) public String scope;
    @DatabaseField() public boolean status;
    @DatabaseField() public String tableName;
}
