package conykais.lib_compiler;

import javax.lang.model.type.TypeMirror;

public class FieldViewBinding {

    private String fieldName;
    private TypeMirror typeName;
    private int viewId;

    public FieldViewBinding() {

    }

    public FieldViewBinding(String fieldName, TypeMirror typeName, int viewId) {
        this.fieldName = fieldName;
        this.typeName = typeName;
        this.viewId = viewId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public TypeMirror getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeMirror typeName) {
        this.typeName = typeName;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

}
