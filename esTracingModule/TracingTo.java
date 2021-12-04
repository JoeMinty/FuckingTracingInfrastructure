package es;

import java.io.Serializable;
import java.util.Date;

public class TracingTo implements Serializable {

    private Long responseTimePerMin = 0L;

    private Long callPerMinute = 0L;

    private Long failCallPerMinute = 0L;

    private Boolean responseTimeAlarmFlag = false;

    private Boolean failCallAlarmFlag = false;

    private String type;

    private String aggregateType;

    private String source;

    private String target;

    private String index;

    private String id;

    private String name;

    private Date createTime;

    private Date updateTime;

    private String label = this.getClass().getSimpleName();

    private Long timeStamp = 0L;

    private Boolean isDeleted = false;

    private String regionId;

    private String cloudId;

    public Long getResponseTimePerMin() {
        return responseTimePerMin;
    }

    public void setResponseTimePerMin(Long responseTimePerMin) {
        this.responseTimePerMin = responseTimePerMin;
    }

    public Long getCallPerMinute() {
        return callPerMinute;
    }

    public void setCallPerMinute(Long callPerMinute) {
        this.callPerMinute = callPerMinute;
    }

    public Long getFailCallPerMinute() {
        return failCallPerMinute;
    }

    public void setFailCallPerMinute(Long failCallPerMinute) {
        this.failCallPerMinute = failCallPerMinute;
    }

    public Boolean getResponseTimeAlarmFlag() {
        return responseTimeAlarmFlag;
    }

    public void setResponseTimeAlarmFlag(Boolean responseTimeAlarmFlag) {
        this.responseTimeAlarmFlag = responseTimeAlarmFlag;
    }

    public Boolean getFailCallAlarmFlag() {
        return failCallAlarmFlag;
    }

    public void setFailCallAlarmFlag(Boolean failCallAlarmFlag) {
        this.failCallAlarmFlag = failCallAlarmFlag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }
}
