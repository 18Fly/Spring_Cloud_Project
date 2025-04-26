package org.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName images
 */
@TableName(value = "images")
@Data
public class Images implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Long uid;
    /**
     *
     */
    private String uimages;
    /**
     *
     */
    private String euimages;
    /**
     *
     */
    private Integer u;
    /**
     *
     */
    private Integer eu;

    public Images(Long uid, String euimages) {
        this.uid = uid;
        this.euimages = euimages;
        this.eu = 1;
    }

    public Images() {
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Images other = (Images) that;
        return (this.getUid() == null ? other.getUid() == null : this.getUid().equals(other.getUid()))
                && (this.getUimages() == null ? other.getUimages() == null : this.getUimages().equals(other.getUimages()))
                && (this.getEuimages() == null ? other.getEuimages() == null : this.getEuimages().equals(other.getEuimages()))
                && (this.getU() == null ? other.getU() == null : this.getU().equals(other.getU()))
                && (this.getEu() == null ? other.getEu() == null : this.getEu().equals(other.getEu()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUid() == null) ? 0 : getUid().hashCode());
        result = prime * result + ((getUimages() == null) ? 0 : getUimages().hashCode());
        result = prime * result + ((getEuimages() == null) ? 0 : getEuimages().hashCode());
        result = prime * result + ((getU() == null) ? 0 : getU().hashCode());
        result = prime * result + ((getEu() == null) ? 0 : getEu().hashCode());
        return result;
    }

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", uid=" + uid +
                ", uimages=" + uimages +
                ", euimages=" + euimages +
                ", u=" + u +
                ", eu=" + eu +
                ", serialVersionUID=" + serialVersionUID +
                "]";
        return sb;
    }
}