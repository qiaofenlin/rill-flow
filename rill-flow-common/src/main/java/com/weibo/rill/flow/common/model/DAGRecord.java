package com.weibo.rill.flow.common.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author fenglin
 * @Description
 * @createTime 2023年09月12日 12:10:00
 */
@Builder
@Data
public class DAGRecord {
    private String businessId;
    private String featureId;
    private String alias;
    private String descriptorId;
    private Long createTime;
    private Long updateTime;
}
