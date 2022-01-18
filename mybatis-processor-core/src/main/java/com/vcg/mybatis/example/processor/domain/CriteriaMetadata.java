package com.vcg.mybatis.example.processor.domain;

import java.util.ArrayList;
import java.util.List;

public class CriteriaMetadata {

    private List<CriterionMetadata> criteria = new ArrayList<>();

    private boolean or;

    public List<CriterionMetadata> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<CriterionMetadata> criteria) {
        this.criteria = criteria;
    }

    public boolean isOr() {
        return or;
    }

    public void setOr(boolean or) {
        this.or = or;
    }
}
