package com.vcg.mybatis.example.processor.visitor;

import com.vcg.mybatis.example.processor.domain.CriterionMetadata;
import javax.lang.model.type.*;

public class QueryTypeVisitor implements TypeVisitor<QueryTypeVisitor, CriterionMetadata> {

    @Override
    public QueryTypeVisitor visit(TypeMirror t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visit(TypeMirror t) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitPrimitive(PrimitiveType t, CriterionMetadata c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitNull(NullType t, CriterionMetadata c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitArray(ArrayType t, CriterionMetadata c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitDeclared(DeclaredType t, CriterionMetadata c) {
        c.setJavaType(t.toString());
        return this;
    }

    @Override
    public QueryTypeVisitor visitError(ErrorType t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitTypeVariable(TypeVariable t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitWildcard(WildcardType t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitExecutable(ExecutableType t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitNoType(NoType t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitUnknown(TypeMirror t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitUnion(UnionType t, CriterionMetadata c) {
        return this;
    }

    @Override
    public QueryTypeVisitor visitIntersection(IntersectionType t, CriterionMetadata c) {
        return this;
    }
}
