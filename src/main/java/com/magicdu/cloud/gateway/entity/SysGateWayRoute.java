package com.magicdu.cloud.gateway.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "sys_gateway_route")
@Entity
@Data
public class SysGateWayRoute {
    @Id
    private Integer id;
    private String serviceId;
    private String uri;
    private String predicates;
    private String filters;
    private String sort;
    private Integer createId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;
    private Integer updateId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Integer updateDate;
    private String remarks;
    private String isDelete;

}
