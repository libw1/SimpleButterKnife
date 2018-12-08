package conykais.lib_compiler;

import com.squareup.javapoet.TypeName;

public class Parameter {
    private int position;
    private TypeName typeName;

    public Parameter(int position, TypeName typeName) {
        this.position = position;
        this.typeName = typeName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }
}
