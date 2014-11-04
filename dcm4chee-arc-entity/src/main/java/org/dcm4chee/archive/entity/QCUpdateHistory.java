package org.dcm4chee.archive.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="qc_update_history")
public class QCUpdateHistory implements Serializable{

    private static final long serialVersionUID = -4407564849668358911L;

    public enum QCUpdateScope {PATIENT, STUDY, SERIES, INSTANCE}

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "pk")
    private long pk;

    @Basic(optional = false)
    @Column(name = "created_time", updatable = false)
    private Date createdTime;

    @Basic(optional = false)
    @Column(name = "object_uid")
    private String ObjectUID;

    @OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true, optional=true)
    @JoinColumn(name = "dicomattrs_fk")
    private AttributesBlob updatedAttributesBlob;

    @Basic(optional = false) @Enumerated(EnumType.STRING)
    @Column(name= "scope", updatable=false)
    private QCUpdateScope scope;

    @OneToOne(optional = true)
    @JoinColumn(name = "qc_update_history_fk")
    private QCUpdateHistory next;

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public AttributesBlob getUpdatedAttributesBlob() {
        return updatedAttributesBlob;
    }

    public void setUpdatedAttributesBlob(AttributesBlob updatedAttributesBlob) {
        this.updatedAttributesBlob = updatedAttributesBlob;
    }

    public QCUpdateScope getScope() {
        return scope;
    }

    public void setScope(QCUpdateScope scope) {
        this.scope = scope;
    }

    public QCUpdateHistory getNext() {
        return next;
    }

    public void setNext(QCUpdateHistory next) {
        this.next = next;
    }

    public long getPk() {
        return pk;
    }

    public String getObjectUID() {
        return ObjectUID;
    }

    public void setObjectUID(String objectUID) {
        ObjectUID = objectUID;
    }

}
